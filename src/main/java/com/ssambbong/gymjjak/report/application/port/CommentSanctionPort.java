package com.ssambbong.gymjjak.report.application.port;

public interface CommentSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
