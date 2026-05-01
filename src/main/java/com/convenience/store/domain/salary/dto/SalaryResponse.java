package com.convenience.store.domain.salary.dto;

import com.convenience.store.domain.salary.entity.Salary;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SalaryResponse {
    private final Long id;
    private final String userName;
    private final int year;
    private final int month;
    private final BigDecimal totalHours;
    private final int baseSalary;
    private final int weeklyHolidayPay;
    private final int totalSalary;
    private final LocalDateTime settledAt;

    public SalaryResponse(Salary salary) {
        this.id = salary.getId();
        this.userName = salary.getUser().getName();
        this.year = salary.getYear();
        this.month = salary.getMonth();
        this.totalHours = salary.getTotalHours();
        this.baseSalary = salary.getBaseSalary();
        this.weeklyHolidayPay = salary.getWeeklyHolidayPay();
        this.totalSalary = salary.getTotalSalary();
        this.settledAt = salary.getSettledAt();
    }
}