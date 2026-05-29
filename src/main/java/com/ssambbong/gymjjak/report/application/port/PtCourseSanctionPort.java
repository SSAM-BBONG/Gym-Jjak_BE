package com.ssambbong.gymjjak.report.application.port;

public interface PtCourseSanctionPort {
    void changeAutoBlind(Long targetId, ReportSanctionAction action);
}
