package com.ssambbong.gymjjak.diet.adapter.out.persistence;

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

class AiNutritionAccessAdapterTest {

    private static final Long USER_ID = 10L;
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-23T00:00:00Z"), ZoneId.of("Asia/Seoul"));

    private SpringDataSubscriptionRepository subscriptionRepository;
    private SpringDataTrainerProfileRepository trainerProfileRepository;
    private AiNutritionAccessAdapter adapter;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SpringDataSubscriptionRepository.class);
        trainerProfileRepository = mock(SpringDataTrainerProfileRepository.class);
        adapter = new AiNutritionAccessAdapter(subscriptionRepository, trainerProfileRepository, CLOCK);
    }

    @Test
    void 활성_구독자는_AI_영양_기능을_사용할_수_있다() {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        when(subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                USER_ID, SubscriptionStatus.ACTIVE, now)).thenReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
        verifyNoInteractions(trainerProfileRepository);
    }

    @Test
    void 구독이_없어도_활성_트레이너는_AI_영양_기능을_사용할_수_있다() {
        when(trainerProfileRepository.existsByUserIdAndStatus(
                USER_ID, TrainerProfileStatus.ACTIVE)).thenReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
        verify(trainerProfileRepository).existsByUserIdAndStatus(USER_ID, TrainerProfileStatus.ACTIVE);
    }

    @Test
    void 구독도_활성_트레이너_프로필도_없으면_사용할_수_없다() {
        assertThat(adapter.hasActiveAccess(USER_ID)).isFalse();
    }
}
