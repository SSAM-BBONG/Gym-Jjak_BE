package com.ssambbong.gymjjak.organization.organizationTrainer.application.query;

public record TrainerDetailView(
        String trainerName,
        double averageRating,
        int reviewCount
) {}
