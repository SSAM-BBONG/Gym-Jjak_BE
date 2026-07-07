package com.ssambbong.gymjjak.report.application.port.ptcourse;

import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;

// PT쪽으로 스냅샷 요청 보내는 포트
public interface PtCourseReportTargetPort {
    ReportTargetSnapshot getSnapshot(Long targetId);
}
