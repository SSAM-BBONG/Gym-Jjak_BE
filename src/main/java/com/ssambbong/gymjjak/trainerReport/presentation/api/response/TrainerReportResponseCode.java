package com.ssambbong.gymjjak.trainerReport.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainerReportResponseCode implements ResponseCode {
    TRAINER_REPORT_LIST("TRAINER_REPORT_200_LIST", "트레이너 리포트 목록 조회 성공"),
    TRAINER_REPORT_DETAIL("TRAINER_REPORT_200_DETAIL", "트레이너 리포트 상세 조회 성공");

    private final String code;
    private final String message;
}
