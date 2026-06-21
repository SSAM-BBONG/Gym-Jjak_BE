package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerAwardResponse(
        @Schema(description = "트레이너 수상 경력 ID", example = "5")
        Long trainerAwardId,

        @Schema(
                description = "수상 또는 대회 경력명",
                example = "2025 피트니스 대회 입상"
        )
        String name
) {
}
