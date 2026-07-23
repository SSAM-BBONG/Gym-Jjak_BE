package com.ssambbong.gymjjak.chatbot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotFastApiAdapterPresenceTest {

    @Test
    void providesDedicatedFastApiClientAdapter() throws ClassNotFoundException {
        Class<?> adapterType = Class.forName(
                "com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out.ai.ChatbotFastApiClientAdapter"
        );

        assertThat(adapterType.getInterfaces())
                .extracting(Class::getName)
                .contains("com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiClientPort");
    }
}
