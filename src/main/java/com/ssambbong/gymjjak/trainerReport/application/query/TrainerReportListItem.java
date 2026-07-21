package com.ssambbong.gymjjak.trainerReport.application.query;

import java.time.LocalDate;

public record TrainerReportListItem(Long trainerReportId, LocalDate targetMonth) {
}
