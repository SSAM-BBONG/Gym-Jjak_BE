package com.ssambbong.gymjjak.payments.payment.application.port;

import java.time.LocalDateTime;

public interface SubscriptionPaymentQueryPort {

    // 활성 구독 존재 여부 (중복 결제 방지용)
    boolean existsActiveByUserId(Long userId, LocalDateTime now);
}
