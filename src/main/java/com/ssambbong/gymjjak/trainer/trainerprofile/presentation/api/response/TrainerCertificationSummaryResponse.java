package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerCertificationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TrainerCertificationSummaryResponse(

        @Schema(
                description = "트레이너 자격증 ID",
                example = "7"
        )
        Long trainerCertificationId,

        @Schema(
                description = "자격증 이름",
                example = "생활스포츠지도사 2급"
        )
        String name,

        @Schema(
                description = "자격증 유형",
                example = "REQUIRED"
        )
        TrainerCertificationType certificationType
) {

    public static TrainerCertificationSummaryResponse from(
            TrainerCertificationSummaryResult result
    ) {
        return TrainerCertificationSummaryResponse.builder()
                .trainerCertificationId(result.trainerCertificationId())
                .name(result.name())
                .certificationType(result.certificationType())
                .build();
    }
}
