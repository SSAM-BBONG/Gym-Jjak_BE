package com.ssambbong.gymjjak.pt.feedback.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FeedbackErrorCode implements ErrorCode {
    // 피드백 조회 실패
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK_001", "피드백을 찾을 수 없습니다."),

    // 본인 강습/예약 피드백이 아닌 경우
    FEEDBACK_FORBIDDEN(HttpStatus.FORBIDDEN, "FEEDBACK_002", "접근 권한이 없습니다."),

    // 커리큘럼 조회 실패
    CURRICULUM_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK_003", "커리큘럼을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
