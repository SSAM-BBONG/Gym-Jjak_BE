package com.ssambbong.gymjjak.report.application.retention;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.report-group")
public record ReportGroupRetentionProperties(
        long periodDays,
        int batchSize
) {

    public ReportGroupRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("신고 그룹의 삭제 기준일이 존재하지 않습니다.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("신고 그룹의 삭제 배치사이즈는 1~500 사이여야합니다.");
        }
    }

    // properties 객체 스스로 계산
    // threshold = now - 180일
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }

}
