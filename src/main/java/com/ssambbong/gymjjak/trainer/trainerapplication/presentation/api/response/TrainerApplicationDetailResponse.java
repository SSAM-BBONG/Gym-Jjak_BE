package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TrainerApplicationDetailResponse(

        @Schema(description = "트레이너 신청 ID. 수정, 관리자 승인/반려 시 사용합니다.", example = "7")
        Long trainerApplicationId,

        @Schema(description = "신청자 유저 ID", example = "1")
        Long userId,

        @Schema(description = "프로필 이미지 URL. 프로필 이미지가 없으면 null입니다.")
        String profileImageUrl,

        @Schema(description = "필수 자격증 조회용 Presigned URL. URL은 1시간 동안 유효합니다.")
        String certificateUrl,

        @Schema(description = "사용자가 입력한 자격증 목록")
        List<String> qualifications,

        @Schema(description = "사용자가 입력한 수상/대회경력 목록")
        List<String> awardHistories,

        @Schema(description = "자기소개")
        String introduction,

        @Schema(description = "트레이너 신청 상태", example = "PENDING")
        TrainerApplicationStatus status,

        @Schema(description = "반려 사유. 반려 상태가 아니면 null입니다.", example = "필수 서류가 불명확합니다.")
        String rejectReason,

        @Schema(description = "처리 관리자 ID. 아직 처리 전이면 null 입니다.", example = "17")
        Long reviewedBy,

        @Schema(description = "리뷰일")
        LocalDateTime reviewedAt,

        @Schema(description = "신청일")
        LocalDateTime createdAt,

        @Schema(description = "수정일")
        LocalDateTime updatedAt

) {

        public static TrainerApplicationDetailResponse from(
                TrainerApplicationDetailResult result,
                String profileImageUrl,
                String certificateUrl
        ) {
                return TrainerApplicationDetailResponse.builder()
                        .trainerApplicationId(result.trainerApplicationId())
                        .userId(result.userId())
                        .profileImageUrl(profileImageUrl)
                        .certificateUrl(certificateUrl)
                        .qualifications(result.qualifications())
                        .awardHistories(result.awardHistories())
                        .introduction(result.introduction())
                        .status(result.status())
                        .rejectReason(result.rejectReason())
                        .reviewedBy(result.reviewedBy())
                        .reviewedAt(result.reviewedAt())
                        .createdAt(result.createdAt())
                        .updatedAt(result.updatedAt())
                        .build();
        }
}
