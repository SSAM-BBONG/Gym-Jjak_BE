package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.user.adapter.out.persistence.SpringDataUserRepository;
import com.ssambbong.gymjjak.user.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AiNutritionAccessAdapterTest {
    private static final Long USER_ID = 2L;
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-21T00:00:00Z"), ZoneId.of("Asia/Seoul"));

    @Mock
    private SpringDataUserRepository userRepository;
    @Mock
    private SpringDataSubscriptionRepository subscriptionRepository;

    private AiNutritionAccessAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AiNutritionAccessAdapter(userRepository, subscriptionRepository, CLOCK);
    }

    @Test
    void 트레이너는_활성_구독이_없어도_AI_식단_분석을_사용할_수_있다() {
        given(userRepository.existsByIdAndRole(USER_ID, UserRole.TRAINER)).willReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
        verifyNoInteractions(subscriptionRepository);
    }

    @Test
    void 일반_사용자는_활성_구독이_있으면_AI_식단_분석을_사용할_수_있다() {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        given(userRepository.existsByIdAndRole(USER_ID, UserRole.TRAINER)).willReturn(false);
        given(subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                USER_ID, SubscriptionStatus.ACTIVE, now)).willReturn(true);

        assertThat(adapter.hasActiveAccess(USER_ID)).isTrue();
    }

    @Test
    void 일반_사용자는_활성_구독이_없으면_AI_식단_분석을_사용할_수_없다() {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        given(userRepository.existsByIdAndRole(USER_ID, UserRole.TRAINER)).willReturn(false);
        given(subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                USER_ID, SubscriptionStatus.ACTIVE, now)).willReturn(false);

        assertThat(adapter.hasActiveAccess(USER_ID)).isFalse();
    }
}
