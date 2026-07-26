package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

public record ChatbotErrorEvent(
        String type,
        String sessionId,
        String requestId,
        String code,
        String message,
        boolean retryable
) {
    public static ChatbotErrorEvent of(
            String sessionId, String requestId, String code, String message, boolean retryable
    ) {
        return new ChatbotErrorEvent("error", sessionId, requestId, code, message, retryable);
    }
}
