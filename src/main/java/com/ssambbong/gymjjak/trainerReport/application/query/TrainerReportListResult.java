package com.ssambbong.gymjjak.trainerReport.application.query;

import java.util.List;

public record TrainerReportListResult(List<TrainerReportListItem> items, boolean hasNext) {
}
