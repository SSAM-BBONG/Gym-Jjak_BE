package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddOrganizationTrainerRequest(
        @NotNull @Positive Long trainerProfileId
) {}
