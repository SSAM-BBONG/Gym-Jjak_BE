package com.ssambbong.gymjjak.global.domain.common.exception;

/* Comment
*   도메인 규칙 위반 예외 클래스
*   - 중복신고
*   - 권한 없음
*   - 상태 전이 불가
*   - etc..
* */
public abstract class BusinessException extends ApplicationException {


    protected BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
