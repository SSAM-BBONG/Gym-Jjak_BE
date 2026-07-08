package com.ssambbong.gymjjak.report.application.port.feedback;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

// 피드백 수동 블라인드 port
public interface FeedbackSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
