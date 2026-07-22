package com.ssambbong.gymjjak.trainerReport.application.usecase;

import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportDetailResult;
import com.ssambbong.gymjjak.trainerReport.application.query.TrainerReportListResult;

public interface TrainerReportQueryUseCase {

    // page는 0부터 시작
    TrainerReportListResult findMyReports(Long userId, int page, int size);

    TrainerReportDetailResult findMyReportDetail(Long userId, Long trainerReportId);
}
