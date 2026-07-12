package com.ssambbong.gymjjak.organization.organization.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;

public enum OrganizationResponseCode implements ResponseCode {

    ORGANIZATION_FOUND("ORG_ORG_200", "조직 정보 조회가 완료되었습니다."),
    ORGANIZATION_LIST_FOUND("ORG_ORG_200_L", "조직 목록 조회가 완료되었습니다."),
    ORGANIZATION_DETAIL_FOUND("ORG_ORG_200_D", "조직 상세 정보 조회가 완료되었습니다."),
    ORGANIZATION_UPDATED("ORG_ORG_200_U", "조직 정보 수정이 완료되었습니다."),
    ORGANIZATION_SEARCH_FOUND("ORG_ORG_200_S", "조직 검색이 완료되었습니다.");

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
