package com.ssambbong.gymjjak.payments.payment.application.port;

import com.ssambbong.gymjjak.payments.subscription.domain.model.SubscriptionPlanType;

import java.time.LocalDateTime;

public interface SubscriptionCreatePort {

    // 결제 완료 후 구독 레코드 생성, 생성된 구독 ID 반환
    Long create(Long userId, SubscriptionPlanType planType, int amount, LocalDateTime startedAt, LocalDateTime expiredAt);
}
