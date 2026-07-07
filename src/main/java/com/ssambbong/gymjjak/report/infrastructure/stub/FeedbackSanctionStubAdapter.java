package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import com.ssambbong.gymjjak.report.application.port.feedback.FeedbackSanctionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// TODO : 구현 후 지워주세요

@Component
@Slf4j
public class FeedbackSanctionStubAdapter implements FeedbackSanctionPort {

    @Override
    public void applySanction(
            Long targetId,
            ReportSanctionAction action
    ) {
        log.warn(
                "event=feedback_sanction_stub_called targetId={}, action={}",
                targetId,
                action
        );

        throw new UnsupportedOperationException(
                "FeedbackSanctionPort 실제 구현체가 아직 연결되지 않았습니다."
        );
    }
}
