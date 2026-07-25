package com.ssambbong.gymjjak.chatbot.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotErrorCodeTest {

    @Test
    void keepsSubscriptionRequiredCodeButUsesGeneralAccessMessage() {
        assertThat(ChatbotErrorCode.SUBSCRIPTION_REQUIRED.getCode())
                .isEqualTo("CHATBOT_SUBSCRIPTION_REQUIRED");
        assertThat(ChatbotErrorCode.SUBSCRIPTION_REQUIRED.getMessage())
                .isEqualTo("챗봇 접근 권한이 필요합니다.");
    }
}
