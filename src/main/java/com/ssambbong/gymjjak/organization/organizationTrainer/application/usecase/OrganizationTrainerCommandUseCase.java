package com.ssambbong.gymjjak.organization.organizationTrainer.application.usecase;

import com.ssambbong.gymjjak.organization.organizationTrainer.application.command.AddOrganizationTrainerCommand;

public interface OrganizationTrainerCommandUseCase {

    Long addTrainer(AddOrganizationTrainerCommand command);

    void removeTrainer(Long organizationAccountId, Long organizationTrainerId);
}
