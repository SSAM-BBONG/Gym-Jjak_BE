package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 제재 상태
@Getter
@RequiredArgsConstructor
public enum ReportGroupSanctionStatus {
    NONE("제재 없음"),
    AUTO_BLINDED("임시 제재"),
    MANUAL_BLINDED("제재");

    private final String description;
}
