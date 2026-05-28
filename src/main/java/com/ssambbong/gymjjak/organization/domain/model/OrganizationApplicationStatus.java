package com.ssambbong.gymjjak.organization.domain.model;

public enum OrganizationApplicationStatus {
    PENDING,    // 검토 중
    ACCEPTED,   // 승인
    REJECTED,   // 반려
    CANCELLED   // 신청자가 직접 취소
}
