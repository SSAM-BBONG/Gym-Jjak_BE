package com.ssambbong.gymjjak.trainer.trainerapplication.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record TrainerApplicationRejectedEvent(
        Long receiverId,
        Long trainerApplicationId,
        String rejectReason,
        Instant occurredAt
) implements DomainEvent {

    public TrainerApplicationRejectedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(trainerApplicationId, "trainerApplicationId는 필수입니다.");
        Objects.requireNonNull(rejectReason, "rejectReason은 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    public TrainerApplicationRejectedEvent(
            Long receiverId,
            Long trainerApplicationId,
            String rejectReason
    ) {
        this(
                receiverId,
                trainerApplicationId,
                rejectReason,
                Instant.now()
        );
    }

}
