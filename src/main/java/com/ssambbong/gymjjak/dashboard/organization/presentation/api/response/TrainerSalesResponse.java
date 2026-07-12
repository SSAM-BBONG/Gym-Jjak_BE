package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerSalesResult;

public record TrainerSalesResponse(
        Long trainerProfileId,
        String trainerName,
        long thisMonthAmount,
        long totalAmount,
        double ratio
) {
    public static TrainerSalesResponse from(TrainerSalesResult result) {
        return new TrainerSalesResponse(
                result.trainerProfileId(),
                result.trainerName(),
                result.thisMonthAmount(),
                result.totalAmount(),
                result.ratio()
        );
    }
}
