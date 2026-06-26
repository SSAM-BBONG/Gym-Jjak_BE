package com.ssambbong.gymjjak.trainerReview.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.trainer-review")
public record TrainerReviewRetentionProperties(
        long periodDays,
        int batchSize
) {
    public TrainerReviewRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("강사평 삭제 기준일이 존재하지 않습니다.");
        }
        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("강사평 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
