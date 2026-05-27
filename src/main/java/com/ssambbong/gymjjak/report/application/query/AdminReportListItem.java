package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportNavigationType;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;

import java.time.LocalDateTime;

public record AdminReportListItem(
        String reportNumber, // uuid 값
        ReportTargetType targetType, // 타겟 타입
        Long targetId, // 타겟 번호
        String targetDisplayText, // 신고 대상 (제목 / 댓글 일부)
        String targetOwnerUsername, // 신고 대상의 아이디
        LocalDateTime reportedAt, // 신고일
        int reportCount, // 누적 신고수
        ReportGroupStatus status, // 처리 상태
        ReportNavigationType navigationType // 네비 타입 (page / modal)
) {
}
