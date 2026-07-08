package com.ssambbong.gymjjak.dashboard.organization.application.query;

public record TrainerClientResult(
        Long trainerProfileId,
        String trainerName,
        double averageRating,
        long clientCount
) {}
