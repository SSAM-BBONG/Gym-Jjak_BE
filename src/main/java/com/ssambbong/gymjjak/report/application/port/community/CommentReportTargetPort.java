package com.ssambbong.gymjjak.report.application.port.community;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

public interface CommentReportTargetPort {

    ReportTargetSnapshot getSnapshot(Long targetId);
}
