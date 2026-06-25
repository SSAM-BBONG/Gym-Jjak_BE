package com.ssambbong.gymjjak.category.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.category")
public record CategoryRetentionProperties(
        long periodDays,
        int batchSize
) {
    public CategoryRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("카테고리 삭제 기준일이 존재하지 않습니다.");
        }
        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("카테고리 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    // 하드딜리트 기준 시간 = now = periodDays
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
