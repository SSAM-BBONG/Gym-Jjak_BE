package com.ssambbong.gymjjak.organization.organizationTrainer.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.organization-trainer")
public record OrganizationTrainerRetentionProperties(
        long periodDays,
        int batchSize
) {
    public OrganizationTrainerRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("조직 트레이너 삭제 기준일이 존재하지 않습니다.");
        }
        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("조직 트레이너 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}
