package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateTrainerApplicationResponse(

        @Schema(
                description = "수정된 트레이너 신청 ID",
                example = "101"
        )
        Long trainerApplicationId
) {
}
