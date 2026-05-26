package com.ssambbong.gymjjak.global.domain.common.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/* Comment
*   침고하세요! 모든 예외 클래스들의 root 클래스
*   ApplicationException 하위 에러
*   1. BusinessException
*   2. InfrastructureException
 * */

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> context = new HashMap<>();

    protected ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected void addContext(String key, Object value) {
        this.context.put(key, value);
    }

    public Map<String, Object> getContext() {
        return Map.copyOf(context);
    }
}
