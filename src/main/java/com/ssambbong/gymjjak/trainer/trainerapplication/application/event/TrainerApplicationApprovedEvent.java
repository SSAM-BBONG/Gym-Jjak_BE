package com.ssambbong.gymjjak.trainer.trainerapplication.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;

public record TrainerApplicationApprovedEvent(
        Long receiverId, // userId
        Long trainerApplicationId, // 신청서 ID
        Long trainerProfileId, // 트레이너 프로필 ID
        Instant occurredAt // 이벤트 발생 시간
) implements DomainEvent {

    public TrainerApplicationApprovedEvent(
            Long receiverId,
            Long trainerApplicationId,
            Long trainerProfileId
    ) {
        this(
                receiverId,
                trainerApplicationId,
                trainerProfileId,
                Instant.now()
        );
    }

}
