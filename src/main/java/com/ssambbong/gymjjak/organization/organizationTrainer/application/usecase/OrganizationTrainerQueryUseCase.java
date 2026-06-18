package com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase;

import com.ssambbong.gymjjak.organization.organizationTrainer.domain.repository.OrganizationTrainerRepository.TrainerSummary;

import java.util.List;

public interface OrganizationTrainerQueryUseCase {

    List<TrainerSummary> findMyOrganizationTrainers(Long organizationAccountId);
}
