package com.convenience.store.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {

    private final WebClient ollamaWebClient;

    public String analyze(String prompt) {
        Map<String, Object> request = Map.of(
                "model", "hermes3",
                "prompt", prompt,
                "stream", false
        );

        Map response = ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String result = response != null ? (String) response.get("response") : "분석 실패";
        log.info("AI 분석 결과: {}", result);
        return result;
    }
}