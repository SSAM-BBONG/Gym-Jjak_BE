package com.ssambbong.gymjjak.report.application.port;

import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

/**
 * 타겟 타입, 타겟 id에 해당하는 신고 대상 정보 가져오는 port
 */
public interface ReportTargetQueryPort {
    ReportTargetSnapshot getSnapshot(ReportTargetType targetType, Long targetId);
}
