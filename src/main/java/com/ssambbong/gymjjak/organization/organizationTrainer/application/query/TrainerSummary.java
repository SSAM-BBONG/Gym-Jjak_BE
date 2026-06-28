package com.ssambbong.gymjjak.organization.organizationTrainer.application.query;

import java.time.LocalDateTime;

public record TrainerSummary(
        Long organizationTrainerId,
        Long trainerProfileId,
        String username,
        String nickname,
        String trainerName,
        LocalDateTime registeredAt
) {}
