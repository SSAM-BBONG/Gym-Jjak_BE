package com.ssambbong.gymjjak.organization.organizationTrainer.presentation.api.response;

import java.time.LocalDateTime;

public record FindOrganizationTrainerResponse(
        Long organizationTrainerId,
        Long trainerProfileId,
        String username,
        String nickname,
        String trainerName,
        LocalDateTime registeredAt
) {}
