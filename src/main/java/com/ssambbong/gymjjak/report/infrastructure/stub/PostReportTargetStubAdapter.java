package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.community.PostReportTargetPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostReportTargetStubAdapter implements PostReportTargetPort {

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.warn("[PostReportTargetStub] 게시글 신고 스냅샷 임시 조회 - postId={}", targetId);

        return new ReportTargetSnapshot(
                targetId,
                0L,
                "게시글 임시 스냅샷",
                "게시글 신고 연동 전 임시 스냅샷입니다.",
                null
        );
    }
}
