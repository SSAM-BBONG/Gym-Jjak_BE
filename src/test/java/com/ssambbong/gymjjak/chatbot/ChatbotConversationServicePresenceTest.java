package com.ssambbong.gymjjak.chatbot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotConversationServicePresenceTest {

    @Test
    void providesApplicationServiceForConversationPreparation() throws ClassNotFoundException {
        Class<?> serviceType = Class.forName(
                "com.ssambbong.gymjjak.chatbot.application.service.ChatbotConversationService"
        );

        assertThat(serviceType.getSimpleName()).isEqualTo("ChatbotConversationService");
    }
}
