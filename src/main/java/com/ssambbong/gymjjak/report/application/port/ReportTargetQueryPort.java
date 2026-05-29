package com.ssambbong.gymjjak.report.application.port;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

public interface ReportTargetQueryPort {
    ReportTargetSnapshot getSnapshot(ReportTargetType targetType, Long targetId);
}
