package com.ssambbong.gymjjak.report.application.port.trainerreview;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

// 트레이너 리뷰 수동 블라인드 port
public interface TrainerReviewSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
