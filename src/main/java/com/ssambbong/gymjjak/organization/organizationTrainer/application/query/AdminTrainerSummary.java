package com.ssambbong.gymjjak.organization.organizationTrainer.application.query;

import java.time.LocalDateTime;

public record AdminTrainerSummary(
        Long organizationTrainerId,
        Long trainerProfileId,
        String trainerName,
        String email,
        LocalDateTime registeredAt
) {}
