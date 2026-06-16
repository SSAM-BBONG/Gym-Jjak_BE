package com.ssambbong.gymjjak.report.application.port;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

// TODO : 추후 여러 게시글이 추가되면, 이 port를 Adapter가 구체화하고, 그 어뎁터에서
//  여러 스냅샷 포트로 쏴주면 됨
public interface ReportSanctionTargetPort {
    void applySanction(ReportTargetType targetType, Long targetId, ReportSanctionAction action);
}
