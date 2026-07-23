package com.ssambbong.gymjjak.chatbot.presentation.websocket.response;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiEvent;

public record ChatbotDoneEvent(
        String type,
        String sessionId,
        String requestId,
        String answer,
        String category,
        String routine,
        String sources,
        boolean limited
) {
    public static ChatbotDoneEvent of(String requestId, ChatbotAiEvent.Done done) {
        return new ChatbotDoneEvent(
                "done", done.sessionId(), requestId, done.answer(), done.category(),
                done.routineJson(), done.sourcesJson(), done.limited()
        );
    }
}
