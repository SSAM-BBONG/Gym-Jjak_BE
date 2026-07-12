package com.ssambbong.gymjjak.payments.payment.infrastructure.adapter;

import com.ssambbong.gymjjak.global.domain.common.exception.InfrastructureException;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentErrorCode;

public class PortOneApiException extends InfrastructureException {

    // PortOne API 호출 실패 (4xx/5xx)
    public PortOneApiException(String message) {
        super(PaymentErrorCode.PORTONE_API_ERROR, message);
    }
}
