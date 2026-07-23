package com.ssambbong.gymjjak.chatbot.presentation.api.response;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;

import java.util.List;

public record ChatbotSessionListResponse(
        List<ChatbotSessionListItemResponse> sessions,
        String nextCursor,
        boolean hasNext
) {
    public static ChatbotSessionListResponse from(ChatbotSessionListResult result) {
        return new ChatbotSessionListResponse(
                result.sessions().stream()
                        .map(ChatbotSessionListItemResponse::from)
                        .toList(),
                result.nextCursor(),
                result.hasNext()
        );
    }
}
