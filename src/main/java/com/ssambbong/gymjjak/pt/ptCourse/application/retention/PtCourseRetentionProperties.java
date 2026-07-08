package com.ssambbong.gymjjak.pt.ptCourse.application.retention;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PtCourseRetentionProperties {

    private static final long DEFAULT_PERIOD_DAYS = 30L;
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final long periodDays;
    private final int batchSize;

    public PtCourseRetentionProperties() {
        this(DEFAULT_PERIOD_DAYS, DEFAULT_BATCH_SIZE);
    }

    public PtCourseRetentionProperties(
            long periodDays,
            int batchSize
    ) {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("PT 강습 삭제 기준일이 존재하지 않습니다.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("PT 강습 삭제 배치사이즈는 1~500 사이여야 합니다.");
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
