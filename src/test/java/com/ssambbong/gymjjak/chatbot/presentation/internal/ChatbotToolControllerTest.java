package com.ssambbong.gymjjak.chatbot.presentation.internal;

import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.chatbot.application.service.ChatbotToolService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatbotToolControllerTest {

    @Test
    void returnsTheLatestInbodyForTheVerifiedInternalRequestContext() {
        ChatbotToolService service = mock(ChatbotToolService.class);
        ChatbotInbodySnapshot snapshot = new ChatbotInbodySnapshot(
                LocalDate.of(2026, 7, 23), new BigDecimal("70.0"), new BigDecimal("20.0"), new BigDecimal("30.0")
        );
        when(service.loadLatestInbody("session-1", "request-1")).thenReturn(snapshot);

        ChatbotToolResponse<ChatbotInbodySnapshot> response = new ChatbotToolController(service)
                .loadLatestInbody("session-1", "request-1").getBody();

        assertThat(response.data()).isEqualTo(snapshot);
    }
}
