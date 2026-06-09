package com.convenience.store.domain.handover.service;

import com.convenience.store.domain.handover.dto.HandoverRequest;
import com.convenience.store.domain.handover.dto.HandoverResponse;
import com.convenience.store.domain.handover.entity.Handover;
import com.convenience.store.domain.handover.repository.HandoverRepository;
import com.convenience.store.domain.user.entity.User;
import com.convenience.store.domain.user.repository.UserRepository;
import com.convenience.store.global.config.OllamaService;
import com.convenience.store.global.config.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandoverService {

    private final HandoverRepository handoverRepository;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final OllamaService ollamaService;

    // 인수인계 작성 (알바생)
    @Transactional
    public HandoverResponse createHandover(String email, HandoverRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Handover handover = Handover.builder()
                .user(user)
                .content(request.getContent())
                .build();

        HandoverResponse response = new HandoverResponse(handoverRepository.save(handover));

        // AI 분석
        String aiResult = analyzeHandover(request.getContent(), user.getName());
        log.info("AI 분석 결과: {}", aiResult);

        // 이상 감지 시에만 텔레그램 전송
        if (aiResult.contains("이상 감지")) {
            String message = String.format(
                    "<b>인수인계 이상 감지</b>\n\n" +
                            "작성자: %s\n" +
                            "내용: %s\n" +
                            "시간: %s\n\n" +
                            "🤖 <b>AI 분석 결과</b>\n%s",
                    user.getName(),
                    request.getContent(),
                    response.getCreatedAt(),
                    aiResult
            );
            telegramService.sendMessage(message);
            log.info("이상 감지 - 텔레그램 알림 전송");
        } else {
            log.info("정상 - 텔레그램 알림 미전송");
        }

        return response;
    }

    // 인수인계 AI 분석
    private String analyzeHandover(String content, String userName) {
        String prompt = String.format(
                "당신은 편의점 관리 AI입니다. 다음 인수인계 내용을 분석하여 " +
                        "재고 부족, 시설 문제, 고객 불만 등 즉각적인 조치가 필요한 사항이 있는지 판단하세요. " +
                        "문제가 있으면 '이상 감지: [문제 내용]' 형식으로 한국어로 짧게 답하고, " +
                        "문제가 없으면 '정상' 이라고만 답하세요.\n\n" +
                        "작성자: %s\n" +
                        "인수인계 내용: %s",
                userName, content
        );
        return ollamaService.analyze(prompt);
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