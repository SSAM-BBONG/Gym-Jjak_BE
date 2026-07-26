package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationPreparation;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotContextJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotMessageJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.ChatbotSessionJpaEntity;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotContextRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotMessageRepository;
import com.ssambbong.gymjjak.chatbot.infrastructure.persistence.SpringDataChatbotSessionRepository;
import com.ssambbong.gymjjak.chatbot.domain.model.ChatbotContextKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotConversationPreparationServiceTest {

    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private SpringDataChatbotMessageRepository messageRepository;
    @Mock private SpringDataChatbotContextRepository contextRepository;
    @Mock private ChatbotSubscriptionAccessPort subscriptionAccessPort;

    private ChatbotConversationPreparationService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotConversationPreparationService(
                sessionRepository, messageRepository, contextRepository, subscriptionAccessPort, new ObjectMapper()
        );
    }

    @Test
    void commitsLightweightRequestStateBeforeThePersonalDataSnapshotIsLoaded() {
        ChatbotSessionJpaEntity session = ChatbotSessionJpaEntity.create(7L, LocalDateTime.now());
        ChatbotMessageJpaEntity latestUserMessage = ChatbotMessageJpaEntity.user(session.getSessionId(), "latest", null);
        ChatbotMessageJpaEntity olderAssistantMessage = ChatbotMessageJpaEntity.assistant(
                session.getSessionId(), "older", "ROUTINE", false
        );
        when(subscriptionAccessPort.hasActiveAccess(7L)).thenReturn(true);
        when(sessionRepository.findBySessionId(session.getSessionId())).thenReturn(Optional.of(session));
        when(messageRepository.findTop12BySessionIdOrderByCreatedAtDesc(session.getSessionId()))
                .thenReturn(List.of(latestUserMessage, olderAssistantMessage));
        when(sessionRepository.acquireStreamLock(eq(session.getSessionId()), eq(7L), any(), any(), any())).thenReturn(1);
        when(contextRepository.findActiveBySessionIdAndUserId(eq(session.getSessionId()), eq(7L), any()))
                .thenReturn(List.of(new ChatbotContextJpaEntity(
                        session.getSessionId(), 7L, ChatbotContextKind.PAIN, "knee pain", null
                )));

        ChatbotConversationPreparation preparation = service.prepare(new SendChatbotMessageCommand(
                session.getSessionId(), 7L, "USER", "routine recommendation", "ROUTINE_RECOMMENDATION", null
        ));

        assertThat(preparation.sessionId()).isEqualTo(session.getSessionId());
        assertThat(preparation.memory().recentMessages()).containsExactly(
                new ChatbotAiRequest.Message("assistant", "older"),
                new ChatbotAiRequest.Message("user", "latest")
        );
        assertThat(preparation.memory().contexts()).containsExactly(
                new ChatbotAiRequest.Context("PAIN", "knee pain")
        );
        ArgumentCaptor<ChatbotMessageJpaEntity> savedMessage = ArgumentCaptor.forClass(ChatbotMessageJpaEntity.class);
        verify(messageRepository).save(savedMessage.capture());
        assertThat(savedMessage.getValue().getContent()).isEqualTo("routine recommendation");
    }
}
