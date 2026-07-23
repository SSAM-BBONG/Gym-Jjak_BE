package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotInbodyQueryPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutHistoryQueryPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotInbodySnapshot;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotToolServiceTest {

    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private ChatbotInbodyQueryPort inbodyQueryPort;
    @Mock private ChatbotWorkoutHistoryQueryPort workoutHistoryQueryPort;

    private ChatbotToolService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotToolService(sessionRepository, inbodyQueryPort, workoutHistoryQueryPort);
    }

    @Test
    void loadsLatestInbodyUsingTheUserResolvedFromTheActiveChatbotSession() {
        ChatbotSessionJpaEntity session = ChatbotSessionJpaEntity.create(7L, LocalDateTime.now());
        ReflectionTestUtils.setField(session, "activeRequestId", "request-1");
        ReflectionTestUtils.setField(session, "activeStreamExpiresAt", LocalDateTime.now().plusSeconds(30));
        when(sessionRepository.findBySessionId(session.getSessionId())).thenReturn(Optional.of(session));

        ChatbotInbodySnapshot expected = new ChatbotInbodySnapshot(
                LocalDate.of(2026, 7, 23), new BigDecimal("70.0"), new BigDecimal("20.0"), new BigDecimal("30.0")
        );
        when(inbodyQueryPort.loadLatest(7L)).thenReturn(expected);

        ChatbotInbodySnapshot result = service.loadLatestInbody(session.getSessionId(), "request-1");

        assertThat(result).isEqualTo(expected);
        verify(inbodyQueryPort).loadLatest(7L);
    }
}
