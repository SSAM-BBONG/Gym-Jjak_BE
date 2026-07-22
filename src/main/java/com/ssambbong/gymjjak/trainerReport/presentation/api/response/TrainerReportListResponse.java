package com.ssambbong.gymjjak.trainerReport.presentation.api.response;

import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportListResult;

import java.util.List;

public record TrainerReportListResponse(List<TrainerReportListItemResponse> items, boolean hasNext) {

    public static TrainerReportListResponse from(TrainerReportListResult result) {
        return new TrainerReportListResponse(
                result.items().stream().map(TrainerReportListItemResponse::from).toList(),
                result.hasNext());
    }
}
