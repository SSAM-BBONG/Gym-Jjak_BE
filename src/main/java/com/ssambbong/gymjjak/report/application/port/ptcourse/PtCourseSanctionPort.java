package com.ssambbong.gymjjak.report.application.port.ptcourse;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

// pt 상태 변경 요청 port
public interface PtCourseSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
