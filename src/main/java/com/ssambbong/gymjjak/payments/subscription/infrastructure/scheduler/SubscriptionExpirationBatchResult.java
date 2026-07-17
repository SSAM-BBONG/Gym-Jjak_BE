package com.ssambbong.gymjjak.payments.subscription.infrastructure.scheduler;

public record SubscriptionExpirationBatchResult(
        int candidateCount,
        int expiredCount,
        int unpaidUserCount
) {
}
