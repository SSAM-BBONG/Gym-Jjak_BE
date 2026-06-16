package com.ssambbong.gymjjak.organization.organization.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrganizationErrorCode implements ErrorCode {

    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORG_ORG_404", "조직을 찾을 수 없습니다."),
    ORGANIZATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORG_ORG_403", "해당 조직에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
