package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;

public enum OrganizationResponseCode implements ResponseCode {

    ORGANIZATION_FOUND("ORG_ORG_200", "조직 정보 조회가 완료되었습니다.");

    private final String code;
    private final String message;

    OrganizationResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
