package com.ssambbong.gymjjak.global.domain.common.exception;

// 요청 또는 설정 값이 잘못했을 때,
public class InvalidArgumentException extends BadRequestException {

    public InvalidArgumentException(String reason) {
        super(CommonErrorCode.INVALID_ARGUMENT);
        addContext("reason", reason);
    }
}
