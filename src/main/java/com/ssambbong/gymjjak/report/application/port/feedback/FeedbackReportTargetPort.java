package com.ssambbong.gymjjak.report.application.port.feedback;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

// 피드백 호출 port
public interface FeedbackReportTargetPort {

    ReportTargetSnapshot getSnapshot(Long targetId);
}
