package com.ssambbong.gymjjak.report.application.port.trainerreview;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

// 트레이너 리뷰 호출 port
public interface TrainerReviewReportTargetPort {

    ReportTargetSnapshot getSnapshot(Long targetId);
}
