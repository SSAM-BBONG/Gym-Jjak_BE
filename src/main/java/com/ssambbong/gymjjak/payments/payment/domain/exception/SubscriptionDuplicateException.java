package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class SubscriptionDuplicateException extends ConflictException {

    // 이미 활성 구독이 존재
    public SubscriptionDuplicateException() {
        super(PaymentErrorCode.SUBSCRIPTION_DUPLICATE);
    }
}
