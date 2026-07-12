package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_DUPLICATE(HttpStatus.CONFLICT, "PAYMENT_001", "이미 구매한 PT 코스입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
