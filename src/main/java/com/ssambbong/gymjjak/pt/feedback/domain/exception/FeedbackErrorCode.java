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
    CURRICULUM_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK_003", "커리큘럼을 찾을 수 없습니다."),

    // 피드백 이미 존재
    FEEDBACK_ALREADY_EXISTS(HttpStatus.CONFLICT, "FEEDBACK_004", "해당 회차에 이미 피드백이 존재합니다."),

    // 미디어 타입 중복 또는 개수 초과
    FEEDBACK_MEDIA_INVALID(HttpStatus.BAD_REQUEST, "FEEDBACK_005", "미디어는 BEFORE/AFTER 각 1개씩만 등록할 수 있습니다."),

    // 예약이 완료 상태이므로 피드백 삭제 불가
    FEEDBACK_RESERVATION_COMPLETED(HttpStatus.CONFLICT, "FEEDBACK_006", "완료된 예약의 피드백은 삭제할 수 없습니다."),

    // 세션이 완료되지 않아 피드백 작성 불가
    FEEDBACK_SESSION_NOT_COMPLETED(HttpStatus.CONFLICT, "FEEDBACK_007", "완료되지 않은 세션에는 피드백을 작성할 수 없습니다."),

    // 예약이 완료 상태이므로 피드백 수정 불가
    FEEDBACK_UPDATE_NOT_ALLOWED(HttpStatus.CONFLICT, "FEEDBACK_008", "완료된 예약의 피드백은 수정할 수 없습니다."),

    // 취소된 예약은 세션이 진행되지 않아 피드백 작성 불가
    FEEDBACK_RESERVATION_CANCELLED(HttpStatus.CONFLICT, "FEEDBACK_009", "취소된 예약에는 피드백을 작성할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
