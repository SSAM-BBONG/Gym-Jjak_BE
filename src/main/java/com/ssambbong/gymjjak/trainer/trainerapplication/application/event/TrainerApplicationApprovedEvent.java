package com.ssambbong.gymjjak.trainer.trainerapplication.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record TrainerApplicationApprovedEvent(
        Long receiverId, // userId
        Long trainerApplicationId, // 신청서 ID
        Long trainerProfileId, // 트레이너 프로필 ID
        Instant occurredAt // 이벤트 발생 시간
) implements DomainEvent {

    public TrainerApplicationApprovedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(trainerApplicationId, "trainerApplicationId는 필수입니다.");
        Objects.requireNonNull(trainerProfileId, "trainerProfileId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

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
