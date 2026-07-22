package com.ssambbong.gymjjak.dashboard.organization.presentation.api.response;

import com.ssambbong.gymjjak.dashboard.organization.application.query.TrainerClientResult;

public record TrainerClientResponse(
        Long trainerProfileId,
        String trainerName,
        long clientCount,
        long ptCount
) {
    public static TrainerClientResponse from(TrainerClientResult result) {
        return new TrainerClientResponse(
                result.trainerProfileId(),
                result.trainerName(),
                result.clientCount(),
                result.ptCount()
        );
    }
}
