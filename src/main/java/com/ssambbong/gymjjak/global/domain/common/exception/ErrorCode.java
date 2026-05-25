package com.ssambbong.gymjjak.global.domain.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "요청이 현재 상태와 충돌합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_001", "알림을 찾을 수 없습니다."),
    NOTIFICATION_ALREADY_READ(HttpStatus.CONFLICT, "NOTIFICATION_002", "이미 읽은 알림입니다."),

    BLACKLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "BLACKLIST_001", "블랙리스트 이력을 찾을 수 없습니다."),
    BLACKLIST_ALREADY_RELEASED(HttpStatus.CONFLICT, "BLACKLIST_002", "이미 해제된 제재입니다."),

    REPORT_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_001", "신고 그룹을 찾을 수 없습니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "REPORT_002", "이미 신고한 대상입니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "REPORT_003", "이미 처리된 신고입니다."),

    ACTION_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "ACTION_LOG_001", "관리자 작업 로그를 찾을 수 없습니다."),
    SYSTEM_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "SYSTEM_LOG_001", "시스템 로그를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
