package com.ssambbong.gymjjak.global.presentation.api.common;

import com.ssambbong.gymjjak.global.domain.common.exception.ApplicationException;
import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID = "traceId";

    // 우리가 만든 비즈니스 예외
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationException(ApplicationException exception) {
        String traceId = getTraceId();

        log.warn("[ApplicationException] code={}, message={}, traceId={}, details={}",
                exception.getErrorCode().getCode(),
                exception.getMessage(),
                traceId,
                exception.getContext()
        );

        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus())
                .body(ApiErrorResponse.of(exception, traceId));
    }

    // @RequestBody, @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String traceId = getTraceId();
        Map<String, Object> details = createFieldErrorDetails(exception.getBindingResult().getFieldErrors());

        log.warn("[MethodArgumentNotValidException] traceId={}, details={}", traceId, details);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(ApiErrorResponse.of(ErrorCode.INVALID_INPUT, traceId, details));
    }

    // @ModelAttribute or query/form binding 검증 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException exception) {
        String traceId = getTraceId();
        Map<String, Object> details = createFieldErrorDetails(exception.getBindingResult().getFieldErrors());

        log.warn("[BindException] traceId={}, details={}", traceId, details);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(ApiErrorResponse.of(ErrorCode.INVALID_INPUT, traceId, details));
    }

    // @RequestParam, @PathVariable 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        String traceId = getTraceId();

        List<Map<String, Object>> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> {
                    Map<String, Object> error = new LinkedHashMap<>();
                    error.put("field", violation.getPropertyPath().toString());
                    error.put("reason", violation.getMessage());
                    return error;
                })
                .toList();

        Map<String, Object> details = Map.of("errors", errors);

        log.warn("[ConstraintViolationException] traceId={}, details={}", traceId, details);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(ApiErrorResponse.of(ErrorCode.INVALID_INPUT, traceId, details));
    }

    // 500 에러! 예상 못한 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception) {
        String traceId = getTraceId();

        log.error("[UnhandledException] traceId={}, message={}", traceId, exception.getMessage(), exception);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, traceId));
    }

    private Map<String, Object> createFieldErrorDetails(List<FieldError> fieldErrors) {
        List<Map<String, Object>> errors = fieldErrors.stream()
                .map(fieldError -> {
                    Map<String, Object> error = new LinkedHashMap<>();
                    error.put("field", fieldError.getField());
                    // rejectedValue 값에는 비번, 토큰 등이 노출이 되는데 빼는게 낫겠죠?
//                    error.put("rejectedValue", fieldError.getRejectedValue());
                    error.put("reason", fieldError.getDefaultMessage());
                    return error;
                })
                .toList();

        return Map.of("errors", errors);
    }

    private String getTraceId() {
        return MDC.get(TRACE_ID);
    }
}
