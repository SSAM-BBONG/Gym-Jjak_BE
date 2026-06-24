package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.query.TrainerProfileDetailResult;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record TrainerProfileDetailResponse(

        @Schema(
                description = "트레이너 프로필 ID",
                example = "7"
        )
        Long trainerProfileId,

        @Schema(
                description = "프로필 이미지 URL",
                nullable = true
        )
        String profileImageUrl,

        @Schema(
                description = "트레이너 이름",
                example = "홍길동"
        )
        String trainerName,

        @Schema(
                description = "트레이너 자기소개",
                example = "안녕하세요, 피지크 전문 양성 트레이너 입니다."
        )
        String introduction,

        @Schema(
                description = "평균 리뷰 평점",
                example = "4.9"
        )
        BigDecimal averageRating,

        @Schema(
                description = "리뷰 개수",
                example = "43"
        )
        int reviewCount,

        @Schema(
                description = "트레이너 프로필 상태",
                example = "ACTIVE"
        )
        TrainerProfileStatus status,

        @Schema(description = "트레이너 자격증 목록")
        List<TrainerCertificationSummaryResponse> certifications,

        @Schema(description = "트레이너 수상 및 대회 경력 목록")
        List<TrainerAwardResponse> awards
) {

    public static TrainerProfileDetailResponse from(
            TrainerProfileDetailResult result
    ) {
        return TrainerProfileDetailResponse.builder()
                .trainerProfileId(result.trainerProfileId())
                .profileImageUrl(result.profileImageUrl())
                .trainerName(result.trainerName())
                .introduction(result.introduction())
                .averageRating(result.averageRating())
                .reviewCount(result.reviewCount())
                .status(result.status())
                .certifications(
                        result.certifications().stream()
                                .map(
                                        TrainerCertificationSummaryResponse::from
                                )
                                .toList()
                )
                .awards(
                        result.awards().stream()
                                .map(
                                        TrainerAwardResponse::from
                                )
                                .toList()
                )
                .build();
    }
}
