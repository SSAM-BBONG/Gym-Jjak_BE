package com.ssambbong.gymjjak.chatbot.application.result;

import java.util.List;

public record ChatbotMessageHistoryResult(
        List<ChatbotMessageHistoryItem> messages,
        String nextCursor,
        boolean hasNext
) {
}
