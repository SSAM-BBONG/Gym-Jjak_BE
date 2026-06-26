package com.ssambbong.gymjjak.trainerReview.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

// application.yml의 app.retention.trainer-review 블록을 바인딩
@ConfigurationProperties(prefix = "app.retention.trainer-review")
public record TrainerReviewRetentionProperties(
        long periodDays, // 소프트딜리트 후 하드딜리트까지 보존 일수 (기본 90일)
        int batchSize    // 1회 실행 시 처리할 최대 행 수 (1~500)
) {
    public TrainerReviewRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("강사평 삭제 기준일이 존재하지 않습니다.");
        }
        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("강사평 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    // 하드딜리트 기준 시각 = now - periodDays
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
