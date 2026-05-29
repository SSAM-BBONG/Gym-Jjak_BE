package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 검토 상태
@Getter
@RequiredArgsConstructor
public enum ReportGroupReviewStatus {
    PENDING("대기중"),
    RESOLVED("처리완료"),
    REJECTED("반려");

    private final String description;
}
