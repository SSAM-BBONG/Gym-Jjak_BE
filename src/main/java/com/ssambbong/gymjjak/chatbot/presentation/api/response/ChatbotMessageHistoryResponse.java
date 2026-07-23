package com.ssambbong.gymjjak.chatbot.presentation.api.response;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;

import java.util.List;

public record ChatbotMessageHistoryResponse(
        List<ChatbotMessageHistoryItemResponse> messages,
        String nextCursor,
        boolean hasNext
) {
    public static ChatbotMessageHistoryResponse from(ChatbotMessageHistoryResult result) {
        return new ChatbotMessageHistoryResponse(
                result.messages().stream()
                        .map(ChatbotMessageHistoryItemResponse::from)
                        .toList(),
                result.nextCursor(),
                result.hasNext()
        );
    }
}
