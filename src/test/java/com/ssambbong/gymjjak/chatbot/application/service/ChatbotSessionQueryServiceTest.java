package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotSessionsQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListItem;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotSessionListResult;
import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotSessionCursorException;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionListRow;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotSessionQueryServiceTest {

    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private ChatbotSessionListRow row;
    @Mock private ChatbotSessionListRow nextRow;

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
        when(row.getSessionId()).thenReturn("session-1");
        when(row.getTitle()).thenReturn("first question");
        when(row.getLastMessage()).thenReturn("latest answer");
        when(row.getLastActivityAt()).thenReturn(lastActivityAt);
        when(sessionRepository.findSessionList(7L, null, null, 21)).thenReturn(List.of(row));

        ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 20));

        assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                "session-1", "first question", "latest answer", lastActivityAt
        ));
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void returnsRequestedSizeAndNextCursorWhenAnExtraRowExists() {
        LocalDateTime firstActivityAt = LocalDateTime.of(2026, 7, 23, 10, 0);
        when(row.getSessionId()).thenReturn("session-2");
        when(row.getTitle()).thenReturn("first question");
        when(row.getLastMessage()).thenReturn("latest answer");
        when(row.getLastActivityAt()).thenReturn(firstActivityAt);
        when(sessionRepository.findSessionList(7L, null, null, 2)).thenReturn(List.of(row, nextRow));

        ChatbotSessionListResult result = service.findSessions(new FindChatbotSessionsQuery(7L, null, 1));

        assertThat(result.sessions()).containsExactly(new ChatbotSessionListItem(
                "session-2", "first question", "latest answer", firstActivityAt
        ));
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotBlank();
        verify(sessionRepository).findSessionList(7L, null, null, 2);
    }

    @Test
    void throwsInvalidCursorExceptionWithoutQueryingRepositoryForMalformedCursor() {
        assertThatThrownBy(() -> service.findSessions(new FindChatbotSessionsQuery(7L, "not-base64", 20)))
                .isInstanceOf(InvalidChatbotSessionCursorException.class);

        verifyNoInteractions(sessionRepository);
    }
}
