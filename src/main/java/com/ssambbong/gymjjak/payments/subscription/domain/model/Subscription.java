package com.ssambbong.gymjjak.payments.subscription.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Subscription {

    private final Long id;
    private final Long userId;
    private final SubscriptionPlanType planType;
    private final long price;
    private final SubscriptionStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime expiredAt;

    private Subscription(
            Long id, Long userId,
            SubscriptionPlanType planType, long price,
            SubscriptionStatus status,
            LocalDateTime startedAt, LocalDateTime expiredAt
    ) {
        this.id = id;
        this.userId = userId;
        this.planType = planType;
        this.price = price;
        this.status = status;
        this.startedAt = startedAt;
        this.expiredAt = expiredAt;
    }

    public static Subscription restore(
            Long id, Long userId,
            SubscriptionPlanType planType, long price,
            SubscriptionStatus status,
            LocalDateTime startedAt, LocalDateTime expiredAt
    ) {
        return new Subscription(id, userId, planType, price, status, startedAt, expiredAt);
    }
}
