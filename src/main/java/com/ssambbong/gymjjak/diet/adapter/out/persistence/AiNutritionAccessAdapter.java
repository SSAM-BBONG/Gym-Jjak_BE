package com.ssambbong.gymjjak.diet.adapter.out.persistence;

import com.ssambbong.gymjjak.diet.application.port.out.AiNutritionAccessPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AiNutritionAccessAdapter implements AiNutritionAccessPort {
    private final SpringDataSubscriptionRepository subscriptionRepository;
    private final Clock clock;

    @Override
    public boolean hasActiveAccess(Long userId) {
        // ACTIVE 상태이면서 만료 시각이 현재보다 뒤인 구독만 유효하게 본다.
        return subscriptionRepository.existsByUserIdAndStatusAndExpiredAtAfter(
                userId, SubscriptionStatus.ACTIVE, LocalDateTime.now(clock));
    }
}
