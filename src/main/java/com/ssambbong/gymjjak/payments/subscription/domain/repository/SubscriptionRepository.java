package com.ssambbong.gymjjak.payments.subscription.domain.repository;

import com.ssambbong.gymjjak.payments.subscription.domain.model.Subscription;

import java.util.Optional;

public interface SubscriptionRepository {

    // 활성 구독 조회
    Optional<Subscription> findActiveByUserId(Long userId);
}
