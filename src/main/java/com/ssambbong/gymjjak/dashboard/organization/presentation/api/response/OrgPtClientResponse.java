package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.OrgPtClientResult;

import java.time.LocalDateTime;

public record OrgPtClientResponse(
        String userName,
        LocalDateTime enrolledAt,
        int progressCount,
        int totalSessionCount
) {
    public static OrgPtClientResponse from(OrgPtClientResult result) {
        return new OrgPtClientResponse(
                result.userName(),
                result.enrolledAt(),
                result.progressCount(),
                result.totalSessionCount()
        );
    }
}
