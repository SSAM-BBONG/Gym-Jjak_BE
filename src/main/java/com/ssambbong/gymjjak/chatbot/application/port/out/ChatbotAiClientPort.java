package com.ssambbong.gymjjak.chatbot.application.port.out;

import java.util.function.Consumer;

public interface ChatbotAiClientPort {

    void stream(ChatbotAiRequest request, Consumer<ChatbotAiEvent> eventConsumer);
}
