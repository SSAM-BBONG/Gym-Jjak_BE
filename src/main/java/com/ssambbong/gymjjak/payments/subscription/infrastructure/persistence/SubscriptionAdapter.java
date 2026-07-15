package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.Subscription;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionAdapter implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository springDataSubscriptionRepository;

    // 활성 구독 조회
    @Override
    public Optional<Subscription> findActiveByUserId(Long userId) {
        return springDataSubscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(SubscriptionJpaEntity::toDomain);
    }
}
