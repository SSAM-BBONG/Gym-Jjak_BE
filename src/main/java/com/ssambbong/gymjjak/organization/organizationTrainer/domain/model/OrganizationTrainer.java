package com.ssambbong.gymjjak.organization.organizationTrainer.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrganizationTrainer {

    private final Long organizationTrainerId;
    private final Long organizationId;
    private final Long trainerProfileId;
    private final Long registeredBy;
    private final LocalDateTime registeredAt;
    private final LocalDateTime removedAt;

    private OrganizationTrainer(
            Long organizationTrainerId,
            Long organizationId,
            Long trainerProfileId,
            Long registeredBy,
            LocalDateTime registeredAt,
            LocalDateTime removedAt
    ) {
        this.organizationTrainerId = organizationTrainerId;
        this.organizationId = organizationId;
        this.trainerProfileId = trainerProfileId;
        this.registeredBy = registeredBy;
        this.registeredAt = registeredAt;
        this.removedAt = removedAt;
    }

    public static OrganizationTrainer restore(
            Long organizationTrainerId,
            Long organizationId,
            Long trainerProfileId,
            Long registeredBy,
            LocalDateTime registeredAt,
            LocalDateTime removedAt
    ) {
        return new OrganizationTrainer(
                organizationTrainerId,
                organizationId,
                trainerProfileId,
                registeredBy,
                registeredAt,
                removedAt
        );
    }

    public boolean isActive() {
        return removedAt == null;
    }
}
