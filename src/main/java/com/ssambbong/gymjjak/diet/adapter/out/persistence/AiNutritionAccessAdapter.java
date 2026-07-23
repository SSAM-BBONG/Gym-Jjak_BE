package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AiNutritionAccessAdapter implements AiNutritionAccessPort {
    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final SpringDataTrainerProfileRepository trainerProfileRepository;
    private final Clock clock;

    @Override
    public boolean hasActiveAccess(Long userId) {
        // ACTIVE 상태이면서 만료 시각이 현재보다 뒤인 구독만 유효하게 본다.
        boolean hasActiveSubscription = subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                userId, SubscriptionStatus.ACTIVE, LocalDateTime.now(clock));
        return hasActiveSubscription
                || trainerProfileRepository.existsByUserIdAndStatus(userId, TrainerProfileStatus.ACTIVE);
    }
}
