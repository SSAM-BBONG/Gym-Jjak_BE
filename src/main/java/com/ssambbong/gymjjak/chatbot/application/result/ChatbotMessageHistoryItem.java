package com.ssambbong.gymjjak.chatbot.application.result;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;

import java.time.LocalDateTime;

public record ChatbotMessageHistoryItem(
        Long messageId,
        ChatbotMessageRole role,
        String content,
        String intentHint,
        String category,
        JsonNode routine,
        JsonNode sources,
        Boolean limited,
        LocalDateTime createdAt
) {
}
