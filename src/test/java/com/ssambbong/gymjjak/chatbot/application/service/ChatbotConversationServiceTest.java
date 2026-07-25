package com.ssambbong.gymjjak.chatbot.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.chatbot.application.command.SendChatbotMessageCommand;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotAiRequest;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotSubscriptionAccessPort;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshot;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotWorkoutData;
import com.ssambbong.gymjjak.chatbot.application.port.out.ChatbotUserDataSnapshotPort;
import com.ssambbong.gymjjak.chatbot.application.result.ChatbotConversationStart;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotErrorCode;
import com.ssambbong.gymjjak.chatbot.exception.ChatbotSessionException;
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
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotConversationServiceTest {

    @Mock private SpringDataChatbotSessionRepository sessionRepository;
    @Mock private SpringDataChatbotMessageRepository messageRepository;
    @Mock private SpringDataChatbotContextRepository contextRepository;
    @Mock private ChatbotSubscriptionAccessPort subscriptionAccessPort;
    @Mock private ChatbotUserDataSnapshotPort userDataSnapshotPort;

    private ChatbotConversationService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotConversationService(
                sessionRepository, messageRepository, contextRepository, subscriptionAccessPort, userDataSnapshotPort,
                new ObjectMapper()
        );
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
        when(subscriptionAccessPort.hasActiveAccess(7L)).thenReturn(true);
        when(userDataSnapshotPort.load(7L)).thenReturn(snapshot());

        ChatbotConversationStart start = service.prepare(new SendChatbotMessageCommand(
                session.getSessionId(), 7L, "USER", "이번 주 루틴 추천", "ROUTINE_RECOMMENDATION", null
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
        assertThat(request.personalData().onboarding().exerciseGoal()).isEqualTo("근력 향상");
        assertThat(request.personalData().onboarding().exercisePeriod()).isEqualTo("6개월 이상");
        assertThat(request.personalData().onboarding().exerciseFrequency()).isEqualTo("주 3회");
        assertThat(request.personalData().onboarding().preferredExercise()).isEqualTo("웨이트 트레이닝");
        assertThat(request.personalData().workoutData().recentWorkouts()).singleElement()
                .satisfies(workout -> assertThat(workout.sets()).singleElement()
                        .satisfies(set -> assertThat(set.weight()).isEqualByComparingTo("60")));
        assertThat(request.personalData().workoutData().summary().partTotalVolumeKg())
                .containsEntry("CHEST", new BigDecimal("600"));

        ArgumentCaptor<ChatbotMessageJpaEntity> savedMessage = ArgumentCaptor.forClass(ChatbotMessageJpaEntity.class);
        verify(messageRepository).save(savedMessage.capture());
        assertThat(savedMessage.getValue().getContent()).isEqualTo("이번 주 루틴 추천");
        verify(sessionRepository).save(session);
    }

    private ChatbotUserDataSnapshot snapshot() {
        return new ChatbotUserDataSnapshot(
                new ChatbotUserDataSnapshot.Onboarding("근력 향상", "6개월 이상", "주 3회", "웨이트 트레이닝"),
                ChatbotWorkoutData.from(List.of(new ChatbotUserDataSnapshot.Workout(
                        LocalDate.of(2026, 7, 25), "CHEST", "벤치프레스",
                        List.of(new ChatbotUserDataSnapshot.WorkoutSet(1, new BigDecimal("60"), 10))
                )), 30, 28),
                List.of(new ChatbotUserDataSnapshot.Inbody(
                        LocalDate.of(2026, 7, 1), new BigDecimal("70"), new BigDecimal("20"), new BigDecimal("30")
                ))
        );
    }

    @Test
    void throwsSubscriptionRequiredWhenUserHasNoActiveChatbotSubscription() {
        when(subscriptionAccessPort.hasActiveAccess(7L)).thenReturn(false);

        assertThatThrownBy(() -> service.prepare(new SendChatbotMessageCommand(
                null, 7L, "USER", "운동 루틴을 추천해 주세요.", "ROUTINE_RECOMMENDATION", null
        )))
                .isInstanceOf(ChatbotSessionException.class)
                .extracting(exception -> ((ChatbotSessionException) exception).getErrorCode())
                .isEqualTo(ChatbotErrorCode.SUBSCRIPTION_REQUIRED);

        verifyNoInteractions(sessionRepository, messageRepository, contextRepository);
    }
}
