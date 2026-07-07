package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;

public enum DashboardResponseCode implements ResponseCode {

    ORG_STATS_FOUND("DASH_200", "헬스장 통계 조회가 완료되었습니다."),
    TRAINER_CLIENTS_FOUND("DASH_201", "트레이너별 수강생 목록 조회가 완료되었습니다.");

    private final String code;
    private final String message;

    DashboardResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
