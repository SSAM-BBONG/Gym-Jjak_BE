package com.ssambbong.gymjjak.global.domain.common.exception;

// 409 리소스 충돌 에러
public abstract class ConflictException extends BusinessException {

    protected ConflictException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    protected ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected ConflictException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
