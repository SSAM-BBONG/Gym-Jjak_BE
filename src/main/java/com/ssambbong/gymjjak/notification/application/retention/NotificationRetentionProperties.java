package com.ssambbong.gymjjak.notification.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.notification")
public record NotificationRetentionProperties(
    long periodDays,
    int batchSize
) {
    public NotificationRetentionProperties {

        if (periodDays <= 0) {
            throw new IllegalArgumentException(
                    "알림 Hard Delete periodDays는 1 이상이여야 합니다."
            );
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException(
                    "알림 Hard Delete batchSize는 1~500 사이여야 합니다."
            );
        }
    }

    // 기준일 계산
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
