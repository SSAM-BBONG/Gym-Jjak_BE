package com.ssambbong.gymjjak.chatbot.domain.model;

import java.time.LocalDateTime;

public record ChatbotSession(String sessionId, Long userId, LocalDateTime lastActivityAt) {

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
