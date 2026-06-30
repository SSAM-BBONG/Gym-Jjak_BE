package com.ssambbong.gymjjak.pt.ptReservation.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record PtReservationCanceledEvent(
        Long receiverId,
        Long ptReservationId,
        Instant occurredAt
) implements DomainEvent {

    public PtReservationCanceledEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(ptReservationId, "ptReservationId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    public PtReservationCanceledEvent(Long receiverId,
                                      Long ptReservationId) {
        this(receiverId, ptReservationId, Instant.now());
    }
}
