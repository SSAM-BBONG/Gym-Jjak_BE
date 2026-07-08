package com.ssambbong.gymjjak.notification.application.retention;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationRetentionProperties {

    private static final long DEFAULT_PERIOD_DAYS = 7L;
    private static final int DEFAULT_BATCH_SIZE = 500;

    private final long periodDays;
    private final int batchSize;

    public NotificationRetentionProperties() {
        this(DEFAULT_PERIOD_DAYS, DEFAULT_BATCH_SIZE);
    }

    public NotificationRetentionProperties(
            long periodDays,
            int batchSize
    ) {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("알림 Hard Delete periodDays는 1 이상이어야 합니다.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("알림 Hard Delete batchSize는 1~500 사이여야 합니다.");
        }

        this.periodDays = periodDays;
        this.batchSize = batchSize;
    }

    public long periodDays() {
        return periodDays;
    }

    public int batchSize() {
        return batchSize;
    }

    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
