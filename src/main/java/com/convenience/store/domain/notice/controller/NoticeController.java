package com.convenience.store.domain.notice.controller;

import com.convenience.store.domain.notice.dto.NoticeRequest;
import com.convenience.store.domain.notice.dto.NoticeResponse;
import com.convenience.store.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 작성 (점장)
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(
                noticeService.createNotice(userDetails.getUsername(), request));
    }

    // 공지사항 수정 (점장)
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.updateNotice(noticeId, request));
    }

    // 공지사항 삭제 (점장)
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    // 전체 공지사항 조회 (점장 + 알바생)
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }
}