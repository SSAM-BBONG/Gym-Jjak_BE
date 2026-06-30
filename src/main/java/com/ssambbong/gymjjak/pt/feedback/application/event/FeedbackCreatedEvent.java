package com.ssambbong.gymjjak.pt.feedback.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

// 피드백 등록 알림 이벤트 레코드
public record FeedbackCreatedEvent(
        Long receiverId,
        Long feedbackId,
        Instant occurredAt
) implements DomainEvent {

    // 생성 시 null 검증
    public FeedbackCreatedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(feedbackId, "feedbackId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    // 편의 생성자
    public FeedbackCreatedEvent(Long receiverId,
                                Long feedbackId) {
        this(receiverId, feedbackId, Instant.now());
    }
}
