package com.convenience.store.domain.salary.controller;

import com.convenience.store.domain.salary.dto.SalaryResponse;
import com.convenience.store.domain.salary.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    // 급여 정산 (점장)
    @PostMapping("/calculate/{userId}")
    public ResponseEntity<SalaryResponse> calculateSalary(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(salaryService.calculateSalary(userId, year, month));
    }

    // 본인 급여 조회 (알바생)
    @GetMapping("/my")
    public ResponseEntity<List<SalaryResponse>> getMySalary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(salaryService.getMySalary(userDetails.getUsername()));
    }

    // 특정 직원 급여 조회 (점장)
    @GetMapping("/{userId}")
    public ResponseEntity<List<SalaryResponse>> getUserSalary(
            @PathVariable Long userId) {
        return ResponseEntity.ok(salaryService.getUserSalary(userId));
    }

    // 전체 직원 급여 조회 (점장)
    @GetMapping("/all")
    public ResponseEntity<List<SalaryResponse>> getAllSalary(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(salaryService.getAllSalary(year, month));
    }
}