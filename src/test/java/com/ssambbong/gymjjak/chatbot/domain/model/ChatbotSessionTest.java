package com.ssambbong.gymjjak.chatbot.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotSessionTest {

    @Test
    void returnsTrueOnlyForItsOwner() {
        ChatbotSession session = new ChatbotSession(
                "session-uuid",
                7L,
                LocalDateTime.of(2026, 7, 23, 10, 0)
        );

        assertThat(session.isOwnedBy(7L)).isTrue();
        assertThat(session.isOwnedBy(8L)).isFalse();
    }
}
