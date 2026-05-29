package com.ssambbong.gymjjak.organization.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;

public enum OrganizationApplicationResponseCode implements ResponseCode {

    ORGANIZATION_APPLICATION_CREATED("ORG_201", "조직 신청이 완료되었습니다."),
    ORGANIZATION_APPLICATION_FOUND("ORG_200", "조직 신청 목록 조회가 완료되었습니다."),
    ORGANIZATION_APPLICATION_DETAILS_FOUND("ORG_200", "조직 신청 상세 조회가 완료되었습니다."),
    ORGANIZATION_APPLICATION_ALL_FOUND("ORG_200", "조직 신청 전체 목록 조회가 완료되었습니다."),
    ORGANIZATION_APPLICATION_APPROVED("ORG_200", "조직 신청이 승인되었습니다.");

    private final String code;
    private final String message;

    OrganizationApplicationResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

}
