package com.ssambbong.gymjjak.report.infrastructure.external;

import com.ssambbong.gymjjak.report.application.port.PtCourseReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PtCourseReportTargetAdapter implements PtCourseReportTargetPort {

    // TODO : 현지가 받아서 처리해주고 삭제할 임시 어뎁터
    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.warn("[PtCourseReportTargetAdapter] 임시 stub 호출 - targetId={}", targetId);
        throw new UnsupportedOperationException("PT 신고 대상 snapshot 조회 구현 필요");
    }
}
