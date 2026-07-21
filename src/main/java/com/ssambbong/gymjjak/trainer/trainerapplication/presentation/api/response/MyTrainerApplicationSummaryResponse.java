package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.MyTrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record MyTrainerApplicationSummaryResponse(

        @Schema(description = "트레이너 신청 ID", example = "7")
        Long trainerApplicationId,

        @Schema(description = "신청한 헬스장명", example = "짐짝 피트니스")
        String organizationName,

        @Schema(description = "신청 상태", example = "PENDING")
        TrainerApplicationStatus status,

        @Schema(description = "신청일시")
        LocalDateTime createdAt,

        @Schema(description = "심사 완료 일시. 심사 전이면 null입니다.")
        LocalDateTime reviewedAt,

        @Schema(description = "반려 사유. 반려 상태가 아니면 null입니다.")
        String rejectReason
) {

    public static MyTrainerApplicationSummaryResponse from(MyTrainerApplicationSummaryResult result) {
        return MyTrainerApplicationSummaryResponse.builder()
                .trainerApplicationId(result.trainerApplicationId())
                .organizationName(result.organizationName())
                .status(result.status())
                .createdAt(result.createdAt())
                .reviewedAt(result.reviewedAt())
                .rejectReason(result.rejectReason())
                .build();
    }
}
