package com.ssambbong.gymjjak.report.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {

    REPORT_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_404_1", "신고 그룹을 찾을 수 없습니다."),
    INVALID_REPORT_TARGET(HttpStatus.BAD_REQUEST, "REPORT_400_1", "유효하지 않은 신고 대상입니다."),
    INVALID_REPORT_TARGET_TYPE(HttpStatus.BAD_REQUEST, "REPORT_400_2", "유효하지 않은 신고 대상 타입입니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "REPORT_409_1", "이미 처리된 신고입니다."),
    REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT_403_1", "해당 신고에 접근할 권한이 없습니다."),
    REPORT_TARGET_OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_404_2", "신고 대상 사용자를 찾을 수 없습니다."),
    REPORT_REASON_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_404_3", "신고 사유 정보를 찾을 수 없습니다."),

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_404_3", "해당 신고를 찾을 수 없습니다."),
    INVALID_REPORT_GROUP_RELATION(HttpStatus.BAD_REQUEST,"REPORT_400_3", "신고 그룹과 신고 정보가 일치하지 않습니다."),

    REPORT_GROUP_EFFECTIVE_COUNT_UNDERFLOW(HttpStatus.CONFLICT,"REPORT_409_2","유효 신고 수는 0보다 작아질 수 없습니다."),

    SELF_REPORT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "REPORT_400_4", "본인이 작성한 대상은 신고할 수 없습니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "REPORT_409_3", "동일한 대상에 중복 신고할 수 없습니다."),

    REPORT_GROUP_SOFT_DELETE_FAILED(HttpStatus.CONFLICT, "REPORT_409_4", "신고 그룹 soft delete 처리에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
