package com.ssambbong.gymjjak.chatbot.application.port.out;

public sealed interface ChatbotAiEvent permits ChatbotAiEvent.Delta, ChatbotAiEvent.Done, ChatbotAiEvent.Error {

    // 진행 중인 스트리밍
    record Delta(String text) implements ChatbotAiEvent {
    }

    // 스트리밍 종료
    record Done(
            String sessionId,
            String answer,
            String category,
            String routineJson,
            String sourcesJson,
            boolean limited,
            String quickRepliesJson
    ) implements ChatbotAiEvent {
    }

    record Error(String code, String message, boolean retryable) implements ChatbotAiEvent {
    }
}
