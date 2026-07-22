package com.ssambbong.gymjjak.trainerReport.application.query;

import java.time.LocalDate;

public record TrainerReportDetailResult(
        Long trainerReportId,
        LocalDate targetMonth,
        String report,
        String marketTrendsSnapshot
) {
}
