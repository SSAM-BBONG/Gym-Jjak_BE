package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PaymentTargetNotFoundException extends NotFoundException {

    public PaymentTargetNotFoundException() {
        super(PaymentErrorCode.PAYMENT_TARGET_NOT_FOUND);
    }
}
