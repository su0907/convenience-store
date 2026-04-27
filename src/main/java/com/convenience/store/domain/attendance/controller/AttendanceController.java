package com.convenience.store.domain.attendance.controller;

import com.convenience.store.domain.attendance.dto.AttendanceResponse;
import com.convenience.store.domain.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출근
    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceResponse> clockIn(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.clockIn(userDetails.getUsername()));
    }

    // 퇴근
    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceResponse> clockOut(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.clockOut(userDetails.getUsername()));
    }

    // 본인 출퇴근 기록 조회
    @GetMapping("/my")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(attendanceService.getMyAttendance(userDetails.getUsername()));
    }

    // 직원별 출퇴근 기록 조회 (점장 전용)
    @GetMapping("/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getUserAttendance(
            @PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getUserAttendance(userId));
    }
}