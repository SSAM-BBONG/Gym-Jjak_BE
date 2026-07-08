package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;

public record TrainerClientResponse(
        Long trainerProfileId,
        String trainerName,
        double averageRating,
        long clientCount
) {
    public static TrainerClientResponse from(TrainerClientResult result) {
        return new TrainerClientResponse(
                result.trainerProfileId(),
                result.trainerName(),
                result.averageRating(),
                result.clientCount()
        );
    }
}
