package com.ssambbong.gymjjak.trainer.trainerapplication.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;

public record TrainerApplicationRejectedEvent(
        Long receiverId,
        Long trainerApplicationId,
        String rejectReason,
        Instant occurredAt
) implements DomainEvent {

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
