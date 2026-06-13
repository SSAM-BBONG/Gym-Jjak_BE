package com.ssambbong.gymjjak.report.application.port;

public interface PtCourseSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
