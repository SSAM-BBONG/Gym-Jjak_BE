package com.ssambbong.gymjjak.global.domain.common.exception;

/* Comment
*   외부 시스템, DB, S3, 메시징 등 기술 예외용 부모 클래스
*   Adapter 계층에서 예외 전환할 때 사용
* */
public abstract class InfrastructureException extends ApplicationException {

    protected InfrastructureException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected InfrastructureException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
