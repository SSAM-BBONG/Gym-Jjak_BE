package com.ssambbong.gymjjak.report.application.port.community;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

public interface PostReportTargetPort {

    // 게시글 신고 스냅샷 메서드
    ReportTargetSnapshot getSnapshot(Long targetId);
}
