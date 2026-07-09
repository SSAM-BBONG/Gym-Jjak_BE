package com.ssambbong.gymjjak.report.infrastructure.stub;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import com.ssambbong.gymjjak.report.application.port.community.CommentReportTargetPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentReportTargetStubAdapter implements CommentReportTargetPort {

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.warn("[CommentReportTargetStub] 댓글 신고 스냅샷 임시 조회 - commentId={}", targetId);

        return new ReportTargetSnapshot(
                targetId,
                0L,
                "댓글 임시 스냅샷",
                "댓글 신고 연동 전 임시 스냅샷입니다.",
                null
        );
    }
}
