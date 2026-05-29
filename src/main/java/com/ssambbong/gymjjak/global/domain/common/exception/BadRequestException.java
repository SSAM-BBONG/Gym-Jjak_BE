package com.ssambbong.gymjjak.global.domain.common.exception;

// 400 접근 권한 없음 에러
public abstract class BadRequestException extends BusinessException {

    protected BadRequestException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    protected BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BadRequestException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
