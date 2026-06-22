package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;

public enum OrganizationTrainerResponseCode implements ResponseCode {

    ORGANIZATION_TRAINER_LIST_FOUND("ORG_OT_200_L", "소속 트레이너 목록 조회가 완료되었습니다."),
    ORGANIZATION_TRAINER_REMOVED("ORG_OT_200_D", "소속 트레이너 삭제가 완료되었습니다.");

    private final String code;
    private final String message;

    OrganizationTrainerResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
