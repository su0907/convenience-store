package com.convenience.store.domain.attendance.dto;

import com.convenience.store.domain.attendance.entity.Attendance;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AttendanceResponse {
    private final Long id;
    private final String userName;
    private final LocalDateTime clockIn;
    private final LocalDateTime clockOut;
    private final BigDecimal workHours;
    private final LocalDate workDate;

    public AttendanceResponse(Attendance attendance) {
        this.id = attendance.getId();
        this.userName = attendance.getUser().getName();
        this.clockIn = attendance.getClockIn();
        this.clockOut = attendance.getClockOut();
        this.workHours = attendance.getWorkHours();
        this.workDate = attendance.getWorkDate();
    }
}