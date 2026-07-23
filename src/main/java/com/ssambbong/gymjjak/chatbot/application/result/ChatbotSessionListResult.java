package com.ssambbong.gymjjak.chatbot.application.result;

import java.util.List;

public record ChatbotSessionListResult(
        List<ChatbotSessionListItem> sessions,
        String nextCursor,
        boolean hasNext
) {
}
