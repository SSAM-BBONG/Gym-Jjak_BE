package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertificationType;
import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerCertificationResponse(

        @Schema(description = "트레이너 자격증 ID", example = "7")
        Long trainerCertificationId,

        @Schema(description = "자격증명", example = "생활스포츠지도사")
        String name,

        @Schema(description = "자격증 유형", example = "REQUIRED")
        TrainerCertificationType certificationType,

        @Schema(
                description = "자격증 파일 URL. 파일이 없는 추가 자격증은 null입니다."
        )
        String fileUrl
) {
}
