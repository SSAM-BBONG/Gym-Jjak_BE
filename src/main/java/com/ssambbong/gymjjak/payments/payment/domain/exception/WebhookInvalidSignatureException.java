package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class WebhookInvalidSignatureException extends BadRequestException {

    public WebhookInvalidSignatureException() {
        super(PaymentErrorCode.WEBHOOK_INVALID_SIGNATURE);
    }
}
