package com.ssambbong.gymjjak.pt.ptReservation.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

// PT 예약 확정 알림 이벤트 레코드
public record PtReservationApprovedEvent(
        Long receiverId, // 예약한 회원 userId
        Long ptReservationId,
        Instant occurredAt
) implements DomainEvent {

    // 생성 시 null 검증
    public PtReservationApprovedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(ptReservationId, "ptReservationId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    // 편의 생성자 (Instant.now() 자동 설정)
    public PtReservationApprovedEvent(Long receiverId,
                                      Long ptReservationId){
        this(receiverId, ptReservationId, Instant.now());
    }
}
