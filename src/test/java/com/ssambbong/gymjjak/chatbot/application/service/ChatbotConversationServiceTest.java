package com.ssambbong.gymjjak.chatbot.application.service;

import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
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
class ChatbotConversationServiceTest {

    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private SpringDataChatbotMessageRepository messageRepository;
    @Mock private SpringDataChatbotContextRepository contextRepository;

    private ChatbotConversationService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotConversationService(sessionRepository, messageRepository, contextRepository);
    }

    @Test
    void preparesFastApiRequestWithOnlyPreviousMessagesInChronologicalOrderAndActiveContexts() {
        ChatbotSessionJpaEntity session = ChatbotSessionJpaEntity.create(7L, LocalDateTime.now());
        ChatbotMessageJpaEntity latestUserMessage = ChatbotMessageJpaEntity.user(
                session.getSessionId(), "최근 질문", null
        );
        ChatbotMessageJpaEntity olderAssistantMessage = ChatbotMessageJpaEntity.assistant(
                session.getSessionId(), "이전 답변", "ROUTINE", false
        );

        when(sessionRepository.findBySessionId(session.getSessionId())).thenReturn(Optional.of(session));
        when(messageRepository.findTop12BySessionIdOrderByCreatedAtDesc(session.getSessionId()))
                .thenReturn(List.of(latestUserMessage, olderAssistantMessage));
        when(sessionRepository.acquireStreamLock(eq(session.getSessionId()), eq(7L), any(), any(), any())).thenReturn(1);
        when(contextRepository.findActiveBySessionIdAndUserId(eq(session.getSessionId()), eq(7L), any()))
                .thenReturn(List.of(new ChatbotContextJpaEntity(
                        session.getSessionId(), 7L, ChatbotContextKind.PAIN, "무릎 통증", null
                )));

        ChatbotConversationStart start = service.prepare(new SendChatbotMessageCommand(
                session.getSessionId(), 7L, "USER", "이번 주 루틴 추천", "ROUTINE_RECOMMENDATION"
        ));

        ChatbotAiRequest request = start.fastApiRequest();
        assertThat(request.sessionId()).isEqualTo(session.getSessionId());
        assertThat(request.actor()).isEqualTo(new ChatbotAiRequest.Actor(7L, "USER"));
        assertThat(request.message()).isEqualTo("이번 주 루틴 추천");
        assertThat(request.memory().recentMessages()).containsExactly(
                new ChatbotAiRequest.Message("assistant", "이전 답변"),
                new ChatbotAiRequest.Message("user", "최근 질문")
        );
        assertThat(request.memory().contexts()).containsExactly(
                new ChatbotAiRequest.Context("PAIN", "무릎 통증")
        );

        ArgumentCaptor<ChatbotMessageJpaEntity> savedMessage = ArgumentCaptor.forClass(ChatbotMessageJpaEntity.class);
        verify(messageRepository).save(savedMessage.capture());
        assertThat(savedMessage.getValue().getContent()).isEqualTo("이번 주 루틴 추천");
        verify(sessionRepository).save(session);
    }
}
