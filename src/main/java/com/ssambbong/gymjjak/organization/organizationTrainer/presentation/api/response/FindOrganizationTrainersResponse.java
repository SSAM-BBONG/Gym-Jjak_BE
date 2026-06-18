package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response;

import java.util.List;

public record FindOrganizationTrainersResponse(
        List<FindOrganizationTrainerResponse> trainers
) {}
