package com.ssambbong.gymjjak.community.application.retention;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommunityRetentionProperties {

    private static final long DEFAULT_PERIOD_DAYS = 90L;
    private static final int DEFAULT_BATCH_SIZE = 500;

    private final long periodDays;
    private final int batchSize;

    public CommunityRetentionProperties() {
        this(DEFAULT_PERIOD_DAYS, DEFAULT_BATCH_SIZE);
    }

    public CommunityRetentionProperties(
            long periodDays,
            int batchSize
    ) {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("Community retention periodDays must be positive.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("Community retention batchSize must be between 1 and 500.");
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
