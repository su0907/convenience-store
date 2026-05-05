package com.convenience.store.domain.notice.service;

import com.convenience.store.domain.notice.dto.NoticeRequest;
import com.convenience.store.domain.notice.dto.NoticeResponse;
import com.convenience.store.domain.notice.entity.Notice;
import com.convenience.store.domain.notice.repository.NoticeRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    // 공지사항 작성 (점장)
    @Transactional
    public NoticeResponse createNotice(String email, NoticeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Notice notice = Notice.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return new NoticeResponse(noticeRepository.save(notice));
    }

    // 공지사항 수정 (점장)
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        notice.update(request.getTitle(), request.getContent());

        return new NoticeResponse(notice);
    }

    // 공지사항 삭제 (점장)
    @Transactional
    public void deleteNotice(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }
        noticeRepository.deleteById(noticeId);
    }

    // 전체 공지사항 조회 (점장 + 알바생)
    public List<NoticeResponse> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NoticeResponse::new)
                .toList();
    }
}