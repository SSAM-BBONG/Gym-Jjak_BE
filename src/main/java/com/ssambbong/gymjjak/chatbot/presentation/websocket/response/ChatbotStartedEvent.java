package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

public record ChatbotStartedEvent(String type, String sessionId, String requestId) {

    public static ChatbotStartedEvent of(String sessionId, String requestId) {
        return new ChatbotStartedEvent("started", sessionId, requestId);
    }
}
