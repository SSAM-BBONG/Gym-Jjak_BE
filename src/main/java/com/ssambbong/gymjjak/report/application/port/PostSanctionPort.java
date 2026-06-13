package com.ssambbong.gymjjak.report.application.port;

public interface PostSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
