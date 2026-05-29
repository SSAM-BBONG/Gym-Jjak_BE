package com.ssambbong.gymjjak.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrganizationApplicationErrorCode implements ErrorCode {

    DUPLICATE_BUSINESS_REGISTRATION_NUMBER(HttpStatus.CONFLICT, "ORG_001", "이미 등록된 사업자 번호입니다."),
    DUPLICATE_REQUESTED_LOGIN_ID(HttpStatus.CONFLICT, "ORG_002", "이미 사용 중인 로그인 ID입니다."),
    ORGANIZATION_APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORG_404", "조직 신청 내역을 찾을 수 없습니다."),
    ORGANIZATION_APPLICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORG_403", "해당 조직 신청에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
