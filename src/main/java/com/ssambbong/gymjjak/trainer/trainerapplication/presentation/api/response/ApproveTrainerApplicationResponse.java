package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApproveTrainerApplicationResponse(

    @Schema(description = "승인된 트레이너 신청 ID", example = "7")
    Long trainerApplicationId,

    @Schema(description = "생성된 트레이너 프로필 ID", example = "3")
    Long trainerProfileId
) {
}
