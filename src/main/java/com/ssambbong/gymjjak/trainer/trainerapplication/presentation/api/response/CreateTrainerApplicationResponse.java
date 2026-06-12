package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTrainerApplicationResponse(
        @Schema(description = "트레이너 신청 ID", example = "1")
        Long trainerApplicationId
) {
}
