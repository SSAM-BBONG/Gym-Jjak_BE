package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentResponseCode implements ResponseCode {

    PAYMENT_PT_CREATED("PAYMENT_PT_CREATED", "PT 결제 요청이 생성되었습니다");

    private final String code;
    private final String message;
}
