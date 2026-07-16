package com.ssambbong.gymjjak.payments.subscription.infrastructure.persistence;

import com.ssambbong.gymjjak.payments.subscription.domain.model.Subscription;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;
import com.ssambbong.gymjjak.payments.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionAdapter implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository springDataSubscriptionRepository;

    @Override
    public Long save(Long userId, SubscriptionPlanType planType, int amount,
                     LocalDateTime startedAt, LocalDateTime expiredAt) {
        return springDataSubscriptionRepository.save(
                new SubscriptionJpaEntity(userId, planType, amount, startedAt, expiredAt)
        ).getId();
    }

    // 활성 구독 조회
    @Override
    public Optional<Subscription> findActiveByUserId(Long userId) {
        return springDataSubscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(SubscriptionJpaEntity::toDomain);
    }
}
