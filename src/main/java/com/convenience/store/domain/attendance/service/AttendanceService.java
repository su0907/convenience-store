package com.convenience.store.domain.attendance.service;

import com.convenience.store.domain.attendance.dto.AttendanceResponse;
import com.convenience.store.domain.attendance.entity.Attendance;
import com.convenience.store.domain.attendance.repository.AttendanceRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 출근
    @Transactional
    public AttendanceResponse clockIn(String email) {
        User user = getUser(email);

        attendanceRepository.findByUserAndClockOutIsNull(user)
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 출근 중입니다.");
                });

        Attendance attendance = Attendance.builder()
                .user(user)
                .clockIn(LocalDateTime.now())
                .workDate(LocalDate.now())
                .build();

        return new AttendanceResponse(attendanceRepository.save(attendance));
    }

    // 퇴근
    @Transactional
    public AttendanceResponse clockOut(String email) {
        User user = getUser(email);

        Attendance attendance = attendanceRepository.findByUserAndClockOutIsNull(user)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        LocalDateTime clockOutTime = LocalDateTime.now();
        BigDecimal workHours = calculateWorkHours(attendance.getClockIn(), clockOutTime);
        attendance.clockOut(clockOutTime, workHours);

        return new AttendanceResponse(attendance);
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