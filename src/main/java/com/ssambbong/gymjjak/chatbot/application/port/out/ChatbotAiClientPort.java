package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.util.function.Consumer;

public interface ChatbotAiClientPort {
    // Consumer : FastAPI에서 온 각 이벤트
    void stream(ChatbotAiRequest request, Consumer<ChatbotAiEvent> eventConsumer);
}
