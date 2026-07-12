package com.ssambbong.gymjjak.dashboard.organization.application.query;

public record TrainerSalesResult(
        Long trainerProfileId,
        String trainerName,
        long thisMonthAmount,
        long totalAmount,
        double ratio
) {}
