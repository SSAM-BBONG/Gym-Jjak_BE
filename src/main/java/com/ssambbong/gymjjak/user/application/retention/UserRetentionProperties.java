package com.ssambbong.gymjjak.user.application.retention;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app.retention.withdrawn-user")
public record UserRetentionProperties(
        long periodDays,
        int batchSize
) {

    public UserRetentionProperties {
        if (periodDays <= 0) {
            throw new IllegalArgumentException("탈퇴 회원의 삭제 기준일이 존재하지 않습니다.");
        }

        if (batchSize <= 0 || batchSize > 500) {
            throw new IllegalArgumentException("탈퇴 회원의 삭제 배치사이즈는 1~500 사이여야 합니다.");
        }
    }

    public LocalDateTime threshold(LocalDateTime now) {
        return now.minusDays(periodDays);
    }
}