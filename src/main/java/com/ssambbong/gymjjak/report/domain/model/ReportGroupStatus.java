package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportGroupStatus {
    // 대기중 = AUTO_BLINDED 상태
    PENDING("대기중"),
    RESOLVED("처리완료"),
    REJECTED("반려");

    private final String description;
}
