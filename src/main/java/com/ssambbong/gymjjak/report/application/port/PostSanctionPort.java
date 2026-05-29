package com.ssambbong.gymjjak.report.application.port;

public interface PostSanctionPort {
    void changeAutoBlind(Long targetId, ReportSanctionAction action);
}
