package com.ssambbong.gymjjak.payments.payment.application.port;

import java.util.Optional;

public interface SubscriptionPaymentQueryPort {

    // 구독 플랜 타입
    Optional<String> findPlanTypeName(Long aiSubscriptionId);
}
