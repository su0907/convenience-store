package com.convenience.store.domain.attendance.entity;

import com.convenience.store.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    @Column(precision = 4, scale = 2)
    private BigDecimal workHours;

    @Column(nullable = false)
    private LocalDate workDate;

    public void clockOut(LocalDateTime clockOut, BigDecimal workHours) {
        this.clockOut = clockOut;
        this.workHours = workHours;
    }
}