package com.convenience.store.domain.attendance.repository;

import com.convenience.store.domain.attendance.entity.Attendance;
import com.convenience.store.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);
    List<Attendance> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);
    Optional<Attendance> findByUserAndClockOutIsNull(User user);
    List<Attendance> findAllByOrderByWorkDateDesc();
}