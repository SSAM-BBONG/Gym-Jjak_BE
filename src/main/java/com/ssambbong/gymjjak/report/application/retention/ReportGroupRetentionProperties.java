package com.ssambbong.gymjjak.report.application.retention;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportGroupRetentionProperties {

    private static final long DEFAULT_PERIOD_DAYS = 180L;
    private static final int DEFAULT_BATCH_SIZE = 500;

    private final long periodDays;
    private final int batchSize;

    public ReportGroupRetentionProperties() {
        this(DEFAULT_PERIOD_DAYS, DEFAULT_BATCH_SIZE);
    }

    public ReportGroupRetentionProperties(
            long periodDays, int batchSize
    ) {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("신고 그룹 삭제 기준일이 존재하지 않습니다.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("신그 그룹의 삭제 배치 사이즈는 1~500 이여야합니다.");
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

    // 수동 블라인드 처리 완료 후 보관 기간이 지난 신고 그룹 삭제 기준 시각
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
