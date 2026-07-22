package com.ssambbong.gymjjak.report.application.port.community;

import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;

public interface PostSanctionPort {
    // 게시글 신고 제재 적용 요청 메서드
    void applySanction(Long targetId, ReportSanctionAction action);
}
