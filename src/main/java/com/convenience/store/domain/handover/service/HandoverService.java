package com.convenience.store.domain.handover.service;

import com.convenience.store.domain.handover.dto.HandoverRequest;
import com.convenience.store.domain.handover.dto.HandoverResponse;
import com.convenience.store.domain.handover.entity.Handover;
import com.convenience.store.domain.handover.repository.HandoverRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HandoverService {

    private final HandoverRepository handoverRepository;
    private final UserRepository userRepository;

    // 인수인계 작성 (알바생)
    @Transactional
    public HandoverResponse createHandover(String email, HandoverRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Handover handover = Handover.builder()
                .user(user)
                .content(request.getContent())
                .build();

        return new HandoverResponse(handoverRepository.save(handover));
    }

    // 당일 인수인계 조회 (점장 + 알바생 공통)
    public List<HandoverResponse> getTodayHandovers() {
        LocalDate today = LocalDate.now();
        return handoverRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(h -> h.getCreatedAt().toLocalDate().equals(today))
                .map(HandoverResponse::new)
                .toList();
    }

    // 전체 인수인계 조회 (점장)
    public List<HandoverResponse> getAllHandovers() {
        return handoverRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(HandoverResponse::new)
                .toList();
    }

    // 일별 인수인계 조회 (점장)
    public List<HandoverResponse> getHandoversByDate(LocalDate date) {
        return handoverRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(h -> h.getCreatedAt().toLocalDate().equals(date))
                .map(HandoverResponse::new)
                .toList();
    }
}