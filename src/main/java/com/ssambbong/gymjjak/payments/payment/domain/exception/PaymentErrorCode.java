package com.ssambbong.gymjjak.payments.payment.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_DUPLICATE(HttpStatus.CONFLICT, "PAYMENT_001", "이미 구매한 PT 코스입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_002", "결제 정보를 찾을 수 없습니다."),
    PORTONE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_003", "PortOne API 호출에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
