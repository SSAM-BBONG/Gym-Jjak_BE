package com.ssambbong.gymjjak.global.domain.common.exception;

// 403 접근 권한 없음 에러
public abstract class BadRequestException extends BusinessException {

    protected BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BadRequestException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
