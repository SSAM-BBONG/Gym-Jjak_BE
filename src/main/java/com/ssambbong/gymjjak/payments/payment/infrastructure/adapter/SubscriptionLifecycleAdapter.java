package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionLifecyclePort;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionLifecycleAdapter implements SubscriptionLifecyclePort {

    private final SpringDataSubscriptionRepository subscriptionRepository;

    @Override
    public void expire(Long subscriptionId) {
        subscriptionRepository.findById(subscriptionId).ifPresent(entity -> entity.expire());
    }
}
