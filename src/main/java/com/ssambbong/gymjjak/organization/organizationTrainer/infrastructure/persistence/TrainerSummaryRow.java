package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import java.time.LocalDateTime;

public interface TrainerSummaryRow {
    Long getOrganizationTrainerId();
    Long getTrainerProfileId();
    String getUsername();
    String getNickname();
    String getTrainerName();
    LocalDateTime getRegisteredAt();
}
