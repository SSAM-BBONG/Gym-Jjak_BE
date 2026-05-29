package com.ssambbong.gymjjak.report.application.port;

public record ReportTargetSnapshot(
        Long targetId,
        Long targetOwnerId,
        String title,
        String content,
        String fileUrl
) {
}
