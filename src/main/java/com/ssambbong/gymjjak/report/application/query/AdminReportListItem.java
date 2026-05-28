package com.ssambbong.gymjjak.report.application.query;

import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
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
        int effectiveReportCount, // 현재 유효 신고 수
        ReportGroupReviewStatus status, // 신고그룹 검토 상태
        ReportNavigationType navigationType // 네비 타입 (page / modal)
) {
}
