package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateTrainerApplicationResponse(
        @Schema(
                description = "생성된 트레이너 신청 ID 목록",
                example = "[101, 102, 103]"
        )
        List<Long> trainerApplicationIds
) {
}
