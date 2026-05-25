package com.ssambbong.gymjjak.global.domain.common.exception;

// 403 사용자 접근 권한 실패 에러
public abstract class ForbiddenException extends BusinessException {

    protected ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected ForbiddenException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}