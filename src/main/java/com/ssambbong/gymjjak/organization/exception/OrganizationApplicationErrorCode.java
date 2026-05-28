package com.ssambbong.gymjjak.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrganizationApplicationErrorCode implements ErrorCode {

    DUPLICATE_BUSINESS_REGISTRATION_NUMBER(HttpStatus.CONFLICT, "ORG_001", "이미 등록된 사업자 번호입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
