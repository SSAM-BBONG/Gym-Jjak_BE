package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PaymentDuplicateException extends ConflictException {

    // 이미 구매한 PT 코스
    public PaymentDuplicateException() {
        super(PaymentErrorCode.PAYMENT_DUPLICATE);
    }
}
