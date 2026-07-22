package com.ssambbong.gymjjak.trainerReport.presentation.api.response;

import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportListItem;

import java.time.LocalDate;

public record TrainerReportListItemResponse(Long trainerReportId, LocalDate targetMonth) {

    public static TrainerReportListItemResponse from(TrainerReportListItem item) {
        return new TrainerReportListItemResponse(item.trainerReportId(), item.targetMonth());
    }
}
