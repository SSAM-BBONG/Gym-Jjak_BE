package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionCreatePort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SubscriptionJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionCreateAdapter implements SubscriptionCreatePort {

    private final SpringDataSubscriptionRepository springDataSubscriptionRepository;

    @Override
    public Long create(Long userId, SubscriptionPlanType planType, int amount, LocalDateTime startedAt, LocalDateTime expiredAt) {
        SubscriptionJpaEntity entity = new SubscriptionJpaEntity(userId, planType, amount, startedAt, expiredAt);
        return springDataSubscriptionRepository.save(entity).getId();
    }
}
