package com.ssambbong.gymjjak.report.application.port.chat;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

public interface ChatSanctionPort {

    void applySanction(Long targetId, ReportSanctionAction action);
}
