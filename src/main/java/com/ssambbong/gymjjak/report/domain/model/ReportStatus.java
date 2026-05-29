package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;
}
