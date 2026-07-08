package com.ssambbong.gymjjak.pt.trainerReview.application.retention;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TrainerReviewRetentionProperties {

    private static final long DEFAULT_PERIOD_DAYS = 90L;
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final long periodDays;
    private final int batchSize;

    public TrainerReviewRetentionProperties() {
        this(DEFAULT_PERIOD_DAYS, DEFAULT_BATCH_SIZE);
    }

    public TrainerReviewRetentionProperties(
            long periodDays,
            int batchSize
    ) {
        if (periodDays <= 0) {
            throw new IllegalArgumentException(
                    "강사평 삭제 기준일은 1일 이상이어야 합니다."
            );
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException(
                    "강사평 삭제 배치 사이즈는 1~500 사이여야 합니다."
            );
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
