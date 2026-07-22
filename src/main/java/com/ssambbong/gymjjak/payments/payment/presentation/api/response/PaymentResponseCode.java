package com.ssambbong.gymjjak.payments.payment.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentResponseCode implements ResponseCode {

    PAYMENT_PT_CREATED("PAYMENT_PT_CREATED", "PT 결제 요청이 생성되었습니다"),
    PAYMENT_SUBSCRIPTION_CREATED("PAYMENT_SUBSCRIPTION_CREATED", "구독 결제 요청이 생성되었습니다"),
    WEBHOOK_RECEIVED("WEBHOOK_RECEIVED", "웹훅 처리 완료"),
    PAYMENTS_FETCHED("PAYMENTS_FETCHED", "결제 내역 조회 성공"),
    PT_PURCHASE_STATUS_FETCHED("PT_PURCHASE_STATUS_FETCHED", "PT 구매 상태 조회 성공");

    private final String code;
    private final String message;
}
