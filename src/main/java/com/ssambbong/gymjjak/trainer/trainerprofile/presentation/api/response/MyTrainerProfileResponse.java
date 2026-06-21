package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record MyTrainerProfileResponse(

        @Schema(description = "트레이너 프로필 ID", example = "3")
        Long trainerProfileId,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "트레이너 이름", example = "홍길동")
        String trainerName,

        @Schema(description = "트레이너 소개")
        String introduction,

        @Schema(description = "평균 평점", example = "4.85")
        BigDecimal averageRating,

        @Schema(description = "리뷰 개수", example = "32")
        int reviewCount,

        @Schema(description = "트레이너 프로필 상태", example = "ACTIVE")
        TrainerProfileStatus status,

        @Schema(description = "트레이너 자격증 목록")
        List<TrainerCertificationResponse> certifications,

        @Schema(description = "트레이너 수상 경력 목록")
        List<TrainerAwardResponse> awards
) {
}
