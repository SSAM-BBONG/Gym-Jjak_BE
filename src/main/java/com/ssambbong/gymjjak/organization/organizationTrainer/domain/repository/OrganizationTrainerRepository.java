package com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerDetailView;
import com.ssambbong.gymjjak.organization.organizationTrainer.application.query.TrainerSummary;
import com.ssambbong.gymjjak.organization.organizationTrainer.domain.model.OrganizationTrainer;

import java.util.List;
import java.util.Optional;

public interface OrganizationTrainerRepository {

    List<OrganizationTrainer> findActiveByOrganizationId(Long organizationId);

    long countActiveByOrganizationId(Long organizationId);

    List<TrainerSummary> findTrainersByOrganizationId(Long organizationId);

    Optional<OrganizationTrainer> findActiveByIdAndOrganizationId(Long organizationTrainerId, Long organizationId);

    void remove(Long organizationTrainerId);

    long countAllActive();

    long countActiveOrganizations();

    List<TrainerDetailView> findTrainerDetailsByOrganizationId(Long organizationId);

    long countAccumulatedMembersByOrganizationId(Long organizationId);
}
