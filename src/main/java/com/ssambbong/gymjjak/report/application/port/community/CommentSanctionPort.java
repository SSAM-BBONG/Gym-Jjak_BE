package com.ssambbong.gymjjak.report.application.port.community;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

public interface CommentSanctionPort {
    void applySanction(Long targetId, ReportSanctionAction action);
}
