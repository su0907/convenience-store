package com.convenience.store.domain.attendance.service;

import com.convenience.store.domain.attendance.dto.AttendanceResponse;
import com.convenience.store.domain.attendance.entity.Attendance;
import com.convenience.store.domain.attendance.repository.AttendanceRepository;
import com.convenience.store.domain.schedule.entity.Schedule;
import com.convenience.store.domain.schedule.repository.ScheduleRepository;
import com.convenience.store.domain.store.service.StoreLocationService;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import com.convenience.store.global.config.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final StoreLocationService storeLocationService;
    private final ScheduleRepository scheduleRepository;
    private final TelegramService telegramService;

    // 출근
    @Transactional
    public AttendanceResponse clockIn(String email, Double latitude, Double longitude) {
        User user = getUser(email);

        // 위치 확인
        if (latitude != null && longitude != null) {
            if (!storeLocationService.isWithinRange(latitude, longitude)) {
                throw new IllegalStateException("편의점 반경 500m 이내에서만 출근할 수 있습니다.");
            }
        }

        attendanceRepository.findByUserAndClockOutIsNull(user)
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 출근 중입니다.");
                });

        Attendance attendance = Attendance.builder()
                .user(user)
                .clockIn(LocalDateTime.now())
                .workDate(LocalDate.now())
                .build();

        AttendanceResponse response = new AttendanceResponse(attendanceRepository.save(attendance));

        // 지각 감지
        checkLate(user, LocalDateTime.now());

        return response;
    }

    // 퇴근
    @Transactional
    public AttendanceResponse clockOut(String email, Double latitude, Double longitude) {
        User user = getUser(email);

        // 위치 확인
        if (latitude != null && longitude != null) {
            if (!storeLocationService.isWithinRange(latitude, longitude)) {
                throw new IllegalStateException("편의점 반경 500m 이내에서만 퇴근할 수 있습니다.");
            }
        }

        Attendance attendance = attendanceRepository.findByUserAndClockOutIsNull(user)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        LocalDateTime clockOutTime = LocalDateTime.now();
        BigDecimal workHours = calculateWorkHours(attendance.getClockIn(), clockOutTime);
        attendance.clockOut(clockOutTime, workHours);

        return new AttendanceResponse(attendance);
    }

    // 지각 감지
    private void checkLate(User user, LocalDateTime clockInTime) {
        LocalDate today = LocalDate.now();
        List<Schedule> schedules = scheduleRepository.findByUserAndWorkDateBetween(user, today, today);

        if (schedules.isEmpty()) {
            log.info("오늘 스케줄 없음 - 지각 감지 스킵");
            return;
        }

        Schedule schedule = schedules.get(0);
        LocalTime scheduledStart = schedule.getStartTime();
        LocalTime actualClockIn = clockInTime.toLocalTime();

        if (actualClockIn.isAfter(scheduledStart)) {
            long lateMinutes = Duration.between(scheduledStart, actualClockIn).toMinutes();
            String message = String.format(
                    "<b>지각 알림</b>\n\n" +
                            "직원: %s\n" +
                            "예정 출근 시간: %s\n" +
                            "실제 출근 시간: %s\n" +
                            "지각 시간: %d분",
                    user.getName(),
                    scheduledStart,
                    actualClockIn,
                    lateMinutes
            );
            telegramService.sendMessage(message);
            log.info("지각 감지 - 텔레그램 알림 전송: {}분 지각", lateMinutes);
        } else {
            log.info("정상 출근 - 지각 아님");
        }
    }

    // 본인 출퇴근 기록 조회
    public List<AttendanceResponse> getMyAttendance(String email) {
        User user = getUser(email);
        return attendanceRepository.findByUser(user)
                .stream()
                .map(AttendanceResponse::new)
                .toList();
    }

    // 직원별 출퇴근 기록 조회 (점장 전용)
    public List<AttendanceResponse> getUserAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return attendanceRepository.findByUser(user)
                .stream()
                .map(AttendanceResponse::new)
                .toList();
    }

    // 전체 직원 출퇴근 기록 조회 (점장 전용)
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAllByOrderByWorkDateDesc()
                .stream()
                .map(AttendanceResponse::new)
                .toList();
    }

    // 근무시간 계산
    private BigDecimal calculateWorkHours(LocalDateTime clockIn, LocalDateTime clockOut) {
        long minutes = Duration.between(clockIn, clockOut).toMinutes();
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
    }
}