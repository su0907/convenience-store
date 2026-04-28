package com.convenience.store.domain.schedule.dto;

import com.convenience.store.domain.schedule.entity.Schedule;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleResponse {
    private final Long id;
    private final String userName;
    private final LocalDate workDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.userName = schedule.getUser().getName();
        this.workDate = schedule.getWorkDate();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
    }
}