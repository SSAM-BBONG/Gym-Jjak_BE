package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.PtCourseReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetQueryPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ssambbong.gymjjak.report.domain.model.ReportTargetType.PT_COURSE;

@Component
@RequiredArgsConstructor
public class ReportTargetQueryAdapter implements ReportTargetQueryPort {

    private final PtCourseReportTargetPort ptCourseReportTargetPort;

    @Override
    public ReportTargetSnapshot getSnapshot(ReportTargetType targetType, Long targetId) {

        return switch (targetType) {
            case PT_COURSE -> ptCourseReportTargetPort.getSnapshot(targetId);
            case POST -> throw new UnsupportedOperationException("POST 신고 대상 조회 포트 구현 필요");
            case COMMENT -> throw new UnsupportedOperationException("COMMENT 신고 대상 조회 포트 구현 필요");
            case FEEDBACK -> throw new UnsupportedOperationException("FEEDBACK 신고 대상 조회 포트 구현 필요");
            case TRAINER_REVIEW -> throw new UnsupportedOperationException("TRAINER_REVIEW 신고 대상 조회 포트 구현 필요");
        };

    }
}
