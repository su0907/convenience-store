package com.convenience.store.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class TelegramService {

    private final WebClient webClient;
    private final String chatId;

    public TelegramService(
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.chat-id}") String chatId) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
        this.chatId = chatId;
    }

    public void sendMessage(String message) {
        try {
            webClient.post()
                    .uri("/sendMessage")
                    .bodyValue(Map.of(
                            "chat_id", chatId,
                            "text", message,
                            "parse_mode", "HTML"
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .subscribe();
            log.info("텔레그램 메시지 전송 완료");
        } catch (Exception e) {
            log.error("텔레그램 메시지 전송 실패: {}", e.getMessage());
        }
    }
}