package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

// 프론트에서 채팅 시작 정보를 알리는 이벤트!
public record ChatbotStartedEvent(String type, String sessionId, String requestId) {

    public static ChatbotStartedEvent of(String sessionId, String requestId) {
        return new ChatbotStartedEvent("started", sessionId, requestId);
    }
}
