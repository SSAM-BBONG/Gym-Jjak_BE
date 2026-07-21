package com.ssambbong.gymjjak.trainerReport.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportDetailResult;

import java.time.LocalDate;

public record TrainerReportDetailResponse(
        Long trainerReportId,
        LocalDate targetMonth,
        String report,
        // 이미 JSON 문자열이므로 다시 이스케이프하지 않고 그대로 내려준다 (프론트 차트용 원본 데이터).
        @JsonRawValue String marketTrendsSnapshot
) {

    public static TrainerReportDetailResponse from(TrainerReportDetailResult result) {
        return new TrainerReportDetailResponse(
                result.trainerReportId(), result.targetMonth(), result.report(), result.marketTrendsSnapshot());
    }
}
