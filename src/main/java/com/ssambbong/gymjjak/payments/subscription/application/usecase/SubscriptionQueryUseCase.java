package com.ssambbong.gymjjak.payments.subscription.application.usecase;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;
import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryUseCase {

    // 구독 플랜 조회
    List<PlanView> findPlans();

    // 내 활성 구독 조회 (없으면 empty)
    Optional<SubscriptionView> findMySubscription(Long userId);

    record PlanView(SubscriptionPlanType planType, int price) {}

    record SubscriptionView(
            SubscriptionPlanType planType,
            SubscriptionStatus status,
            LocalDateTime startedAt,
            LocalDateTime expiredAt
    ) {}
}
