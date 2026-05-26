package com.ssambbong.gymjjak.global.domain.common.exception;

// 401 인증/권한 요청 거부 에러
public abstract class UnauthorizedException extends BusinessException {

    protected UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected UnauthorizedException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}