package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.util.List;

// 실제 fastAPI 서버로 보낼 요청
public record ChatbotAiRequest(
        String sessionId,
        String message,
        String intentHint,
        Actor actor,    // user or ai
        Memory memory,  // 이전 기록
        String requestId
) {
    // 응답 주체
    public record Actor(Long userId, String role) {
    }

    // 이전 대화 요약
    public record Memory(
            String summary, // DB에 저장된 사용자 이전 대화 요약
            List<Message> recentMessages,
            List<Context> contexts
    ) {
    }

    //
    public record Message(String role, String content) {
    }

    public record Context(String kind, String value) {
    }
}
