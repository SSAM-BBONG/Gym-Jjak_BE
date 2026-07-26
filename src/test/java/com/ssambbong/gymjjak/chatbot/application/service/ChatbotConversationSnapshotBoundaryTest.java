package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationPreparation;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotContextRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotConversationSnapshotBoundaryTest {

    @Mock private ChatbotConversationPreparationService preparationService;
    @Mock private ChatbotUserDataSnapshotService snapshotService;
    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private SpringDataChatbotMessageRepository messageRepository;
    @Mock private SpringDataChatbotContextRepository contextRepository;

    private ChatbotConversationService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotConversationService(
                preparationService, snapshotService, sessionRepository,
                messageRepository, contextRepository, new ObjectMapper()
        );
    }

    @Test
    void loadsSnapshotOnlyAfterTransactionalPreparationHasCompleted() {
        SendChatbotMessageCommand command = command();
        when(preparationService.prepare(command)).thenReturn(preparation());
        when(snapshotService.load(7L)).thenReturn(snapshot());

        service.prepare(command);

        InOrder order = inOrder(preparationService, snapshotService);
        order.verify(preparationService).prepare(command);
        order.verify(snapshotService).load(7L);
    }

    @Test
    void releasesOnlyPreparedRequestLockWhenSnapshotLoadingFails() {
        SendChatbotMessageCommand command = command();
        ChatbotConversationPreparation preparation = preparation();
        when(preparationService.prepare(command)).thenReturn(preparation);
        when(snapshotService.load(7L)).thenThrow(new IllegalStateException("snapshot failed"));

        assertThatThrownBy(() -> service.prepare(command))
                .isInstanceOf(IllegalStateException.class);

        verify(preparationService).releaseStreamLock(preparation.sessionId(), preparation.requestId());
    }

    private SendChatbotMessageCommand command() {
        return new SendChatbotMessageCommand("session-1", 7L, "USER", "루틴 추천", "ROUTINE_RECOMMENDATION", null);
    }

    private ChatbotConversationPreparation preparation() {
        return new ChatbotConversationPreparation(
                "session-1", "request-1", "루틴 추천", "ROUTINE_RECOMMENDATION", "USER",
                new ChatbotAiRequest.Memory(null, List.of(), List.of())
        );
    }

    private ChatbotUserDataSnapshot snapshot() {
        return ChatbotUserDataSnapshot.empty();
    }
}
