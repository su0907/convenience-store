package com.convenience.store.domain.handover.dto;

import com.convenience.store.domain.handover.entity.Handover;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HandoverResponse {
    private final Long id;
    private final String userName;
    private final String content;
    private final LocalDateTime createdAt;

    public HandoverResponse(Handover handover) {
        this.id = handover.getId();
        this.userName = handover.getUser().getName();
        this.content = handover.getContent();
        this.createdAt = handover.getCreatedAt();
    }
}