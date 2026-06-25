package com.ssambbong.gymjjak.organization.organizationTrainer.infrastructure.persistence;

import java.time.LocalDateTime;

public interface TrainerSummaryRow {
    Long getOrganizationTrainerId();
    Long getTrainerProfileId();
    String getUsername();
    String getEmail();
    String getTrainerName();
    LocalDateTime getRegisteredAt();
}
