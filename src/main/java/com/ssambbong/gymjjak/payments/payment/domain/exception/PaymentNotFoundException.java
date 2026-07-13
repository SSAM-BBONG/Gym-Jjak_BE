package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {

    // orderId로 결제 건을 조회했으나 존재하지 않을 때
    public PaymentNotFoundException() {
        super(PaymentErrorCode.PAYMENT_NOT_FOUND);
    }
}
