package com.ssambbong.gymjjak.chatbot.presentation.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryItem;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;

import java.time.LocalDateTime;

public record ChatbotMessageHistoryItemResponse(
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
    public static ChatbotMessageHistoryItemResponse from(ChatbotMessageHistoryItem item) {
        return new ChatbotMessageHistoryItemResponse(
                item.messageId(),
                item.role(),
                item.content(),
                item.intentHint(),
                item.category(),
                item.routine(),
                item.sources(),
                item.limited(),
                item.createdAt()
        );
    }
}
