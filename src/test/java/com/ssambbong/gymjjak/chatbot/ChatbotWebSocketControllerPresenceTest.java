package com.ssambbong.gymjjak.chatbot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotWebSocketControllerPresenceTest {

    @Test
    void providesDedicatedWebSocketController() throws ClassNotFoundException {
        Class<?> controllerType = Class.forName(
                "com.ssambbong.gymjjak.chatbot.presentation.websocket.ChatbotWebSocketController"
        );

        assertThat(controllerType.getSimpleName()).isEqualTo("ChatbotWebSocketController");
    }
}
