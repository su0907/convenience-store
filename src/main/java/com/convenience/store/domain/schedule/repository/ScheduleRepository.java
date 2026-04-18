package com.convenience.store.domain.schedule.repository;

import com.convenience.store.domain.schedule.entity.Schedule;
import com.convenience.store.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUser(User user);
    List<Schedule> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);
    List<Schedule> findByWorkDateBetween(LocalDate start, LocalDate end);
}