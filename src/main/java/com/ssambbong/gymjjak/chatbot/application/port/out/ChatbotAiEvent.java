package com.ssambbong.gymjjak.chatbot.application.port.out;

public sealed interface ChatbotAiEvent permits ChatbotAiEvent.Delta, ChatbotAiEvent.Done, ChatbotAiEvent.Error {

    record Delta(String text) implements ChatbotAiEvent {
    }

    record Done(
            String sessionId,
            String answer,
            String category,
            String routineJson,
            String sourcesJson,
            boolean limited
    ) implements ChatbotAiEvent {
    }

    record Error(String code, String message, boolean retryable) implements ChatbotAiEvent {
    }
}
