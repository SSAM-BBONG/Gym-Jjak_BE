package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationSummaryResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainerApplicationSummaryResponse(

        @Schema(description = "트레이너 신청 ID.", example = "7")
        Long trainerApplicationId,

        @Schema(description = "신청자 유저 ID.", example = "2")
        Long userId,

        @Schema(description = "신청자 이메일", example = "pending1@example.com")
        String username,

        @Schema(description = "신청자 이름", example = "신청자1")
        String name,

        @Schema(description = "신청자 닉네임", example = "헬린이")
        String nickname,

        @Schema(description = "트레이너 신청 상태", example = "PENDING")
        TrainerApplicationStatus status,

        @Schema(description = "신청일")
        LocalDateTime createdAt,

        @Schema(description = "처리일. 처리 전이면 null입니다.")
        LocalDateTime reviewedAt
) {

    public static TrainerApplicationSummaryResponse from(TrainerApplicationSummaryResult result) {
        return TrainerApplicationSummaryResponse.builder()
                .trainerApplicationId(result.trainerApplicationId())
                .userId(result.userId())
                .username(result.username())
                .name(result.name())
                .nickname(result.nickname())
                .status(result.status())
                .createdAt(result.createdAt())
                .reviewedAt(result.reviewedAt())
                .build();
    }
}
