package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionCreatePort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import com.ssambbong.gymjjak.payments.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCreateAdapter implements SubscriptionCreatePort {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Long create(Long userId, SubscriptionPlanType planType, int amount, LocalDateTime startedAt, LocalDateTime expiredAt) {
        return subscriptionRepository.save(userId, planType, amount, startedAt, expiredAt);
    }
}
