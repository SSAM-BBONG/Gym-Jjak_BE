package com.ssambbong.gymjjak.payments.subscription.domain.model;

import java.time.LocalDateTime;

public enum SubscriptionPlanType {
    MONTHLY,
    YEARLY;

    public int price() {
        return switch (this) {
            case MONTHLY -> 4900;
            case YEARLY -> 49000;
        };
    }

    public LocalDateTime expiresAt(LocalDateTime startedAt) {
        return switch (this) {
            case MONTHLY -> startedAt.plusMonths(1);
            case YEARLY -> startedAt.plusYears(1);
        };
    }
}
