package com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;

import java.time.LocalDateTime;
import java.util.List;

public interface OrganizationTrainerRepository {

    List<OrganizationTrainer> findActiveByOrganizationId(Long organizationId);

    long countActiveByOrganizationId(Long organizationId);

    List<TrainerSummary> findTrainersByOrganizationId(Long organizationId);

    record TrainerSummary(
            Long organizationTrainerId,
            Long trainerProfileId,
            String username,
            String trainerName,
            LocalDateTime registeredAt
    ) {}
}
