package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectTrainerApplicationRequest(

        @Schema(description = "트레이너 신청 반려 사유",
        example = "자격증 이미지가 불명확하여 확인할 수 없습니다.")
        @NotBlank(message = "반려 사유는 필수입니다.")
        @Size(
                max = 500,
                message = "반려 사유는 최대 500자까지 입력할 수 있습니다."
        )
        String rejectReason
) {
}
