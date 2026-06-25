package com.ssambbong.gymjjak.pt.ptCourse.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.pt-course")
public record PtCourseRetentionProperties(
        long periodDays, // 소프트딜리트 후 하드딜리트까지 보존 일수 (기본 30일)
        int batchSize    // 1회 실행 시 처리할 최대 행 수 (1~500)
) {
    public PtCourseRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("PT 강습 삭제 기준일이 존재하지 않습니다.");
        }
        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("PT 강습 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    // 하드딜리트 기준 시각 = now - periodDays
    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
