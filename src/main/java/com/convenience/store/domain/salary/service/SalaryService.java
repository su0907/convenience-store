package com.convenience.store.domain.salary.service;

import com.convenience.store.domain.attendance.entity.Attendance;
import com.convenience.store.domain.attendance.repository.AttendanceRepository;
import com.convenience.store.domain.salary.dto.SalaryResponse;
import com.convenience.store.domain.salary.entity.Salary;
import com.convenience.store.domain.salary.repository.SalaryRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 급여 정산 (점장)
    @Transactional
    public SalaryResponse calculateSalary(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        // 이미 정산된 경우 반환
        salaryRepository.findByUserAndYearAndMonth(user, year, month)
                .ifPresent(s -> {
                    throw new IllegalStateException("이미 정산된 급여입니다.");
                });

        // 해당 월 출퇴근 기록 조회
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Attendance> attendances = attendanceRepository
                .findByUserAndWorkDateBetween(user, start, end);

        // 총 근무시간 계산
        BigDecimal totalHours = attendances.stream()
                .filter(a -> a.getWorkHours() != null)
                .map(Attendance::getWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 기본급 계산
        int baseSalary = totalHours
                .multiply(BigDecimal.valueOf(user.getHourlyWage()))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        // 주휴수당 계산
        int weeklyHolidayPay = calculateWeeklyHolidayPay(attendances, user.getHourlyWage());

        // 최종 급여
        int totalSalary = baseSalary + weeklyHolidayPay;

        Salary salary = Salary.builder()
                .user(user)
                .year(year)
                .month(month)
                .totalHours(totalHours)
                .baseSalary(baseSalary)
                .weeklyHolidayPay(weeklyHolidayPay)
                .totalSalary(totalSalary)
                .build();

        return new SalaryResponse(salaryRepository.save(salary));
    }

    // 본인 급여 조회 (알바생)
    public List<SalaryResponse> getMySalary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return salaryRepository.findByUser(user)
                .stream()
                .map(SalaryResponse::new)
                .toList();
    }

    // 특정 직원 급여 조회 (점장)
    public List<SalaryResponse> getUserSalary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return salaryRepository.findByUser(user)
                .stream()
                .map(SalaryResponse::new)
                .toList();
    }

    // 전체 직원 급여 조회 (점장)
    public List<SalaryResponse> getAllSalary(int year, int month) {
        return salaryRepository.findByYearAndMonth(year, month)
                .stream()
                .map(SalaryResponse::new)
                .toList();
    }

    // 주휴수당 계산 로직
    private int calculateWeeklyHolidayPay(List<Attendance> attendances, int hourlyWage) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        // 주차별 근무시간 합산
        Map<Integer, BigDecimal> weeklyHours = attendances.stream()
                .filter(a -> a.getWorkHours() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getWorkDate().get(weekFields.weekOfYear()),
                        Collectors.reducing(BigDecimal.ZERO,
                                Attendance::getWorkHours,
                                BigDecimal::add)
                ));

        // 주 15시간 이상인 주차에만 주휴수당 지급
        // 주휴수당 = (주 근무시간 / 40) * 8 * 시급
        return weeklyHours.values().stream()
                .filter(hours -> hours.compareTo(BigDecimal.valueOf(15)) >= 0)
                .map(hours -> hours
                        .divide(BigDecimal.valueOf(40), 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(8))
                        .multiply(BigDecimal.valueOf(hourlyWage))
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue())
                .mapToInt(Integer::intValue)
                .sum();
    }
}