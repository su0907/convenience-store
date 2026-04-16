package com.convenience.store.domain.salary.entity;

import com.convenience.store.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal totalHours;

    @Column(nullable = false)
    private int baseSalary;

    @Column(nullable = false)
    private int weeklyHolidayPay;

    @Column(nullable = false)
    private int totalSalary;

    @Column(nullable = false, updatable = false)
    private LocalDateTime settledAt;

    @PrePersist
    protected void onCreate() {
        this.settledAt = LocalDateTime.now();
    }
}