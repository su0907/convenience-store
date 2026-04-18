package com.convenience.store.domain.salary.repository;

import com.convenience.store.domain.salary.entity.Salary;
import com.convenience.store.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByUser(User user);
    Optional<Salary> findByUserAndYearAndMonth(User user, int year, int month);
    List<Salary> findByYearAndMonth(int year, int month);
}