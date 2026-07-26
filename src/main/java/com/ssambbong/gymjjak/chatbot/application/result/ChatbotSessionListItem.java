package com.ssambbong.gymjjak.chatbot.application.result;

import java.time.LocalDateTime;

public record ChatbotSessionListItem(
        String sessionId,
        String title,
        String lastMessage,
        LocalDateTime lastActivityAt
) {
}
