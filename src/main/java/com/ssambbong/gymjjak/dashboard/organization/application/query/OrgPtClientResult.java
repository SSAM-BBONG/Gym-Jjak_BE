package com.ssambbong.gymjjak.dashboard.organization.application.query;

import java.time.LocalDateTime;

public record OrgPtClientResult(
        String userName,
        LocalDateTime enrolledAt,
        int progressCount,
        int totalSessionCount
) {
}
