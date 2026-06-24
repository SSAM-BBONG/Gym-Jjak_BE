package com.ssambbong.gymjjak.organization.organizationTrainer.application.command;

public record AddOrganizationTrainerCommand(
        Long organizationAccountId,
        Long trainerProfileId
) {}
