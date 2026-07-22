package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.util.List;

public record ChatbotAiRequest(
        String sessionId,
        String message,
        String intentHint,
        Actor actor,
        Memory memory,
        String requestId
) {
    public record Actor(Long userId, String role) {
    }

    public record Memory(String summary, List<Message> recentMessages, List<Context> contexts) {
    }

    public record Message(String role, String content) {
    }

    public record Context(String kind, String value) {
    }
}
