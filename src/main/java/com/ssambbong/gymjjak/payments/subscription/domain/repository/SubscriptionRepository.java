package com.ssambbong.gymjjak.payments.subscription.domain.repository;

import com.ssambbong.gymjjak.payments.subscription.domain.model.Subscription;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SubscriptionRepository {

    Long save(Long userId, SubscriptionPlanType planType, int amount,
              LocalDateTime startedAt, LocalDateTime expiredAt);

    // 활성 구독 조회
    Optional<Subscription> findActiveByUserId(Long userId, LocalDateTime now);
}
