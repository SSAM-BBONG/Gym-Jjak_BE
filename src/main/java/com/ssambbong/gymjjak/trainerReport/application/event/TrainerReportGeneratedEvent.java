package com.ssambbong.gymjjak.trainerReport.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

// 트레이너 리포트 생성 완료 알림 이벤트 레코드
public record TrainerReportGeneratedEvent(
        Long receiverId, // 리포트를 받는 트레이너의 userId
        Long trainerReportId,
        Instant occurredAt
) implements DomainEvent {

    public TrainerReportGeneratedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(trainerReportId, "trainerReportId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    public TrainerReportGeneratedEvent(Long receiverId, Long trainerReportId) {
        this(receiverId, trainerReportId, Instant.now());
    }
}
