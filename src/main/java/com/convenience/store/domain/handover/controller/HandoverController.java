package com.convenience.store.domain.handover.controller;

import com.convenience.store.domain.handover.dto.HandoverRequest;
import com.convenience.store.domain.handover.dto.HandoverResponse;
import com.convenience.store.domain.handover.service.HandoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/handover")
@RequiredArgsConstructor
public class HandoverController {

    private final HandoverService handoverService;

    // 인수인계 작성 (알바생)
    @PostMapping
    public ResponseEntity<HandoverResponse> createHandover(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody HandoverRequest request) {
        return ResponseEntity.ok(
                handoverService.createHandover(userDetails.getUsername(), request));
    }

    // 당일 인수인계 조회 (점장 + 알바생 공통)
    @GetMapping("/today")
    public ResponseEntity<List<HandoverResponse>> getTodayHandovers() {
        return ResponseEntity.ok(handoverService.getTodayHandovers());
    }

    // 전체 인수인계 조회 (점장)
    @GetMapping("/all")
    public ResponseEntity<List<HandoverResponse>> getAllHandovers() {
        return ResponseEntity.ok(handoverService.getAllHandovers());
    }

    // 일별 인수인계 조회 (점장)
    @GetMapping("/date")
    public ResponseEntity<List<HandoverResponse>> getHandoversByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(handoverService.getHandoversByDate(date));
    }
}