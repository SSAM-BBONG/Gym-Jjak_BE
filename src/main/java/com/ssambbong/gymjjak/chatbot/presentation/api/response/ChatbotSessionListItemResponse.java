package com.ssambbong.gymjjak.chatbot.presentation.api.response;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;

import java.time.LocalDateTime;

public record ChatbotSessionListItemResponse(
        String sessionId,
        String title,
        String lastMessage,
        LocalDateTime lastActivityAt
) {
    public static ChatbotSessionListItemResponse from(ChatbotSessionListItem item) {
        return new ChatbotSessionListItemResponse(
                item.sessionId(),
                item.title(),
                item.lastMessage(),
                item.lastActivityAt()
        );
    }
}
