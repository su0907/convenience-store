package com.convenience.store.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ScheduleRequest {
    private Long userId;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
}