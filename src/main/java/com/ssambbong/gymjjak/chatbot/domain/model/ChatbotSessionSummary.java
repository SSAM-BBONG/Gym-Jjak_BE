package com.ssambbong.gymjjak.chatbot.domain.model;

import java.time.LocalDateTime;

public record ChatbotSessionSummary(
        String sessionId,
        String title,
        String lastMessage,
        LocalDateTime lastActivityAt
) {
}
