package com.ssambbong.gymjjak.chatbot.domain.model;

import java.time.LocalDateTime;

public record ChatbotMessage(
        Long messageId,
        String sessionId,
        ChatbotMessageRole role,
        String content,
        String intentHint,
        String category,
        String routineJson,
        String sourcesJson,
        Boolean limited,
        LocalDateTime createdAt
) {
}
