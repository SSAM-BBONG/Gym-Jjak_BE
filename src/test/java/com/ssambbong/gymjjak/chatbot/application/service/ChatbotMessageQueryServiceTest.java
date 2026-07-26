package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.query.FindChatbotMessagesQuery;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotMessageHistoryResult;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessage;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotMessageRole;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotSession;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.domain.repository.ChatbotSessionRepository;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import com.ssambbong.gymjjak.chatbot.exception.InvalidChatbotMessageCursorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotMessageQueryServiceTest {

    private static final long USER_ID = 7L;
    private static final String SESSION_ID = "session-1";

    @Mock private ChatbotSessionRepository sessionRepository;
    @Mock private ChatbotMessageRepository messageRepository;

    private ChatbotMessageQueryService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotMessageQueryService(
                sessionRepository,
                messageRepository,
                new ChatbotMessageCursorCodec(new ObjectMapper().findAndRegisterModules()),
                new ObjectMapper().findAndRegisterModules()
        );
    }

    @Test
    void returnsMessagesOldestFirstAndNextCursorWhenAnExtraMessageExists() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 23, 10, 0);
        ChatbotMessage newest = message(3L, createdAt.plusMinutes(2), "newest");
        ChatbotMessage middle = message(2L, createdAt.plusMinutes(1), "middle");
        ChatbotMessage oldest = message(1L, createdAt, "oldest");
        ownedSession();
        when(messageRepository.findHistory(SESSION_ID, null, null, 3))
                .thenReturn(List.of(newest, middle, oldest));

        ChatbotMessageHistoryResult result = service.findMessages(
                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 2)
        );

        assertThat(result.messages()).extracting(item -> item.messageId()).containsExactly(2L, 3L);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotBlank();
        verify(messageRepository).findHistory(SESSION_ID, null, null, 3);
    }

    @Test
    void returnsNoNextCursorWhenMessagesFitRequestedSize() {
        ChatbotMessage onlyMessage = message(1L, LocalDateTime.of(2026, 7, 23, 10, 0), "only");
        ownedSession();
        when(messageRepository.findHistory(SESSION_ID, null, null, 3)).thenReturn(List.of(onlyMessage));

        ChatbotMessageHistoryResult result = service.findMessages(
                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 2)
        );

        assertThat(result.messages()).extracting(item -> item.messageId()).containsExactly(1L);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void returnsEmptyHistoryWithoutNextCursorWhenOwnedSessionHasNoMessages() {
        ownedSession();
        when(messageRepository.findHistory(SESSION_ID, null, null, 21)).thenReturn(List.of());

        ChatbotMessageHistoryResult result = service.findMessages(
                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)
        );

        assertThat(result.messages()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void throwsSessionNotFoundWithoutQueryingMessageHistory() {
        when(sessionRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)))
                .isInstanceOf(ChatbotSessionException.class)
                .hasMessageContaining("챗봇 세션");

        verifyNoInteractions(messageRepository);
    }

    @Test
    void throwsAccessDeniedWithoutQueryingMessageHistoryForAnotherUsersSession() {
        when(sessionRepository.findBySessionId(SESSION_ID))
                .thenReturn(Optional.of(new ChatbotSession(SESSION_ID, USER_ID + 1, LocalDateTime.now())));

        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)))
                .isInstanceOf(ChatbotSessionException.class);

        verifyNoInteractions(messageRepository);
    }

    @Test
    void throwsInvalidCursorExceptionWithoutQueryingMessageHistoryForMalformedCursor() {
        ownedSession();

        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, "not-base64", 20)))
                .isInstanceOf(InvalidChatbotMessageCursorException.class);
        assertThatThrownBy(() -> service.findMessages(new FindChatbotMessagesQuery(USER_ID, SESSION_ID, "bnVsbA", 20)))
                .isInstanceOf(InvalidChatbotMessageCursorException.class);

        verifyNoInteractions(messageRepository);
    }

    @Test
    void mapsAssistantMetadataAndUsesEmptyArrayForNullSources() {
        ChatbotMessage assistant = new ChatbotMessage(
                1L, SESSION_ID, ChatbotMessageRole.ASSISTANT, "answer", null, "ROUTINE",
                "{\"name\":\"routine\"}", null, false, LocalDateTime.of(2026, 7, 23, 10, 0)
        );
        ownedSession();
        when(messageRepository.findHistory(SESSION_ID, null, null, 21)).thenReturn(List.of(assistant));

        ChatbotMessageHistoryResult result = service.findMessages(
                new FindChatbotMessagesQuery(USER_ID, SESSION_ID, null, 20)
        );

        assertThat(result.messages().get(0).routine().get("name").asText()).isEqualTo("routine");
        assertThat(result.messages().get(0).sources().isArray()).isTrue();
        assertThat(result.messages().get(0).sources()).isEmpty();
    }

    private void ownedSession() {
        when(sessionRepository.findBySessionId(SESSION_ID))
                .thenReturn(Optional.of(new ChatbotSession(SESSION_ID, USER_ID, LocalDateTime.now())));
    }

    private ChatbotMessage message(Long id, LocalDateTime createdAt, String content) {
        return new ChatbotMessage(
                id, SESSION_ID, ChatbotMessageRole.USER, content, null, null,
                null, null, null, createdAt
        );
    }
}
