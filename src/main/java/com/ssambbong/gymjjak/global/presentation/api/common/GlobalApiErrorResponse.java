package com.ssambbong.gymjjak.global.presentation.api.common;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public record GlobalApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String traceId,
        Map<String, Object> details
) {

    // 직접 만든 비즈니스 예외가 발생했을 때
    // 직접 만든 커스텀 도메인 Exception 클래스
    public static GlobalApiErrorResponse of(ApplicationException exception, String traceId) {
        return new GlobalApiErrorResponse(
                LocalDateTime.now(),
                exception.getErrorCode().getHttpStatus().value(),
                exception.getErrorCode().getCode(),
                exception.getMessage(),
                traceId,
                exception.getContext()
        );
    }

    // 예외 객체 없이 ErrorCode만으로 에러 응답을 만들 때
    // 500 에러나, 접근 거부, 인증 실패
    public static GlobalApiErrorResponse of(ErrorCode errorCode, String traceId) {
        return new GlobalApiErrorResponse(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                traceId,
                Map.of()
        );
    }

    // ErrorCode에 추가 상세 정보를 붙이고 싶을 때
    // Validation 오류 필드 목록 표시 같은거!
    public static GlobalApiErrorResponse of(
            ErrorCode errorCode,
            String traceId,
            Map<String, Object> details
    ) {
        return new GlobalApiErrorResponse(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                traceId,
                details == null ? Map.of() : details
        );
    }
}
