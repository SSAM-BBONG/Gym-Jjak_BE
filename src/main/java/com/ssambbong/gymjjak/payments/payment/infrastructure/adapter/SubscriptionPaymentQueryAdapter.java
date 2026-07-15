package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentQueryAdapter implements SubscriptionPaymentQueryPort {

    private final SpringDataSubscriptionRepository springDataSubscriptionRepository;

    // 활성 구독 존재 여부
    @Override
    public boolean existsActiveByUserId(Long userId) {
        return springDataSubscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }
}
