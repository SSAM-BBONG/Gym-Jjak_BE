package com.ssambbong.gymjjak.report.application.port;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

public interface ReportSanctionTargetPort {
    void changeAutoBlind(ReportTargetType targetType, Long targetId, ReportSanctionAction action);
}
