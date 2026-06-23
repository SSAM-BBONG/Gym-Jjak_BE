package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateTrainerProfileResponse(

        @Schema(
                description = "수정된 트레이너 프로필 ID",
                example = "7"
        )
        Long trainerProfileId
) {
}
