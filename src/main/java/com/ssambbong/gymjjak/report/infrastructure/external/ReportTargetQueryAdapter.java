package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.ReportTargetQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportTargetQueryAdapter implements ReportTargetQueryPort {


    @Override
    public ReportTargetSnapshot getSnapshot(ReportTargetType targetType, Long targetId) {
        return null;
    }
}
