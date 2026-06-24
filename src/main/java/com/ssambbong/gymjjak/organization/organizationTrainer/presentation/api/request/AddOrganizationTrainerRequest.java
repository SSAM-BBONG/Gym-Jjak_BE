package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.request;

import jakarta.validation.constraints.NotNull;

public record AddOrganizationTrainerRequest(
        @NotNull Long trainerProfileId
) {}
