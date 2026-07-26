package com.ssambbong.gymjjak.chatbot.infrastructure.adapter.out;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ChatbotSubscriptionAccessAdapterTest {

    private static final Long USER_ID = 10L;
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-26T00:00:00Z"), ZoneId.of("Asia/Seoul")
    );

    private SpringDataSubscriptionRepository subscriptionRepository;
    private SpringDataTrainerProfileRepository trainerProfileRepository;
    private ChatbotSubscriptionAccessAdapter adapter;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SpringDataSubscriptionRepository.class);
        trainerProfileRepository = mock(SpringDataTrainerProfileRepository.class);
        adapter = new ChatbotSubscriptionAccessAdapter(subscriptionRepository, trainerProfileRepository, CLOCK);
    }

    @Test
    void allowsUserWithActiveSubscription() {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        when(subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                USER_ID, SubscriptionStatus.ACTIVE, now)).thenReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
        verifyNoInteractions(trainerProfileRepository);
    }

    @Test
    void allowsActiveTrainerProfileWithoutSubscription() {
        when(trainerProfileRepository.existsByUserIdAndStatus(
                USER_ID, TrainerProfileStatus.ACTIVE)).thenReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
        verify(trainerProfileRepository).existsByUserIdAndStatus(USER_ID, TrainerProfileStatus.ACTIVE);
    }

    @Test
    void deniesUserWithoutActiveSubscriptionOrActiveTrainerProfile() {
        assertThat(adapter.hasActiveAccess(USER_ID)).isFalse();
    }
}
