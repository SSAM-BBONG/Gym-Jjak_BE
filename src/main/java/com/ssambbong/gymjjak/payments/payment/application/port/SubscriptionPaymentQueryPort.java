package com.ssambbong.gymjjak.payments.payment.application.port;

import java.util.Optional;

public interface SubscriptionPaymentQueryPort {

    // 구독 플랜 타입
    Optional<String> findPlanTypeName(Long aiSubscriptionId);

    // 활성 구독 존재 여부 (중복 결제 방지용)
    boolean existsActiveByUserId(Long userId);
}
