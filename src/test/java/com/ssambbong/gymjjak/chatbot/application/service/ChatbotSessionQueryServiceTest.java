package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSessionSummary;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotSessionCursorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotSessionQueryServiceTest {

    @Mock private ChatbotSessionRepository sessionRepository;

    private ChatbotSessionQueryService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotSessionQueryService(
                sessionRepository,
                new ChatbotSessionCursorCodec(new ObjectMapper().findAndRegisterModules())
        );
    }

    @Test
    void mapsSessionRowAndReturnsNoNextCursorWhenRowsFitRequestedSize() {
        LocalDateTime lastActivityAt = LocalDateTime.of(2026, 7, 23, 10, 0);
        ChatbotSessionSummary summary = new ChatbotSessionSummary(
                "session-1", "first question", "latest answer", lastActivityAt
        );
        when(sessionRepository.findSessionSummaries(7L, null, null, 21)).thenReturn(List.of(summary));

        ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 20));

        assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                "session-1", "first question", "latest answer", lastActivityAt
        ));
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
        verify(sessionRepository).findSessionSummaries(7L, null, null, 21);
    }

    @Test
    void returnsRequestedSizeAndNextCursorWhenAnExtraRowExists() {
        LocalDateTime firstActivityAt = LocalDateTime.of(2026, 7, 23, 10, 0);
        ChatbotSessionSummary summary = new ChatbotSessionSummary(
                "session-2", "first question", "latest answer", firstActivityAt
        );
        ChatbotSessionSummary nextSummary = new ChatbotSessionSummary(
                "session-1", "older question", "older answer", firstActivityAt.minusMinutes(1)
        );
        when(sessionRepository.findSessionSummaries(7L, null, null, 2)).thenReturn(List.of(summary, nextSummary));

        ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 1));

        assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                "session-2", "first question", "latest answer", firstActivityAt
        ));
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotBlank();
        verify(sessionRepository).findSessionSummaries(7L, null, null, 2);
    }

    @Test
    void throwsInvalidCursorExceptionWithoutQueryingRepositoryForMalformedCursor() {
        assertThatThrownBy(() -> service.findSessions(new FindChatbotSessionsQuery(7L, "not-base64", 20)))
                .isInstanceOf(InvalidChatbotSessionCursorException.class);

        verifyNoInteractions(sessionRepository);
    }

    @Test
    void throwsInvalidCursorExceptionWithoutQueryingRepositoryForJsonNullCursor() {
        assertThatThrownBy(() -> service.findSessions(new FindChatbotSessionsQuery(7L, "bnVsbA", 20)))
                .isInstanceOf(InvalidChatbotSessionCursorException.class);

        verifyNoInteractions(sessionRepository);
    }
}
