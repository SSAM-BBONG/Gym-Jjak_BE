package com.ssambbong.gymjjak.report.application.port.chat;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

public interface ChatReportTargetPort {

    ReportTargetSnapshot getSnapshot(Long targetId);
}
