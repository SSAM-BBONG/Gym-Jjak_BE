package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.query.TrainerApplicationReviewDetailResult;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record TrainerApplicationReviewDetailResponse(

        @Schema(description = "트레이너 신청 ID", example = "7")
        Long trainerApplicationId,

        @Schema(description = "신청한 유저 ID", example = "2")
        Long userId,

        @Schema(description = "프로필 이미지 URL. 프로필 이미지가 없으면 null입니다.")
        String profileImageUrl,

        @Schema(description = "신청자 이름", example = "김정수")
        String name,

        @Schema(description = "신청자 이메일/로그인 ID", example = "user02@test.com")
        String username,

        @Schema(description = "신청자 닉네임", example = "운동초보")
        String nickname,

        @Schema(description = "자기소개")
        String introduction,

        @Schema(description = "신청자가 입력한 자격증 목록")
        List<String> qualifications,

        @Schema(description = "필수 자격증 조회용 Presigned URL. URL은 1시간 동안 유효합니다.")
        String certificateUrl,

        @Schema(description = "신청자가 입력한 수상/대회경력 목록")
        List<String> awardHistories,

        @Schema(description = "트레이너 신청 상태", example = "PENDING")
        TrainerApplicationStatus status
) {

    public static TrainerApplicationReviewDetailResponse from(
            TrainerApplicationReviewDetailResult result,
            String profileImageUrl,
            String certificateUrl
            ) {
        return TrainerApplicationReviewDetailResponse.builder()
                .trainerApplicationId(result.trainerApplicationId())
                .userId(result.userId())
                .profileImageUrl(profileImageUrl)
                .name(result.name())
                .username(result.username())
                .nickname(result.nickname())
                .introduction(result.introduction())
                .qualifications(result.qualifications())
                .certificateUrl(certificateUrl)
                .awardHistories(result.awardHistories())
                .status(result.status())
                .build();
    }
}
