package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackReportTargetPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// TODO : 구현 후 지워주세요
@Component
@Slf4j
public class FeedbackReportTargetStubAdapter implements FeedbackReportTargetPort {

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.warn(
                "event=feedback_report_target_stub_called targetId={}",
                targetId
        );

        throw new UnsupportedOperationException(
                "FeedbackReportTargetPort 실제 구현체가 아직 연결되지 않았습니다."
        );
    }
}
