package com.ssambbong.gymjjak.payments.subscription.application.usecase;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;

import java.util.List;

public interface SubscriptionQueryUseCase {

    // 구독 플랜 조회
    List<PlanView> findPlans();

    record PlanView(SubscriptionPlanType planType, int price) {}
}
