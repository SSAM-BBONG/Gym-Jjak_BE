package com.ssambbong.gymjjak.payments.payment.application.port;

public interface SubscriptionUserPort {

    // 동일 사용자의 구독 결제와 상태 변경을 직렬화하기 위해 사용자 행을 잠근다.
    void lockById(Long userId);

    void markAsPaid(Long userId);

    void markAsUnpaid(Long userId);
}
