package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.payments.payment.application.port.SubscriptionPaymentQueryPort;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentQueryAdapter implements SubscriptionPaymentQueryPort {

    private final SpringDataSubscriptionRepository springDataSubscriptionRepository;

    // 구독 플랜 타입 조회
    @Override
    public Optional<String> findPlanTypeName(Long aiSubscriptionId) {
        return springDataSubscriptionRepository.findById(aiSubscriptionId)
                .map(entity -> entity.getPlanType().name());
    }

    // 활성 구독 존재 여부
    @Override
    public boolean existsActiveByUserId(Long userId) {
        return springDataSubscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }
}
