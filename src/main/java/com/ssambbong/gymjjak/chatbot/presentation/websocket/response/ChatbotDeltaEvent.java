package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

public record ChatbotDeltaEvent(String type, String sessionId, String requestId, String text) {

    public static ChatbotDeltaEvent of(String sessionId, String requestId, String text) {
        return new ChatbotDeltaEvent("delta", sessionId, requestId, text);
    }
}
