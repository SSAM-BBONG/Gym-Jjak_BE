package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateTrainerApplicationRequest(

        @Schema(description = "프로필 이미지 파일 ID. PROFILE_IMAGE 타입으로 업로드/등록한 fileId를 전달합니다.")
        Long profileImageFileId,

        @Schema(
                hidden = true,
                description = "필수 자격증 파일 ID는 수정할 수 없습니다. 이 값이 전달되면 400 에러가 발생합니다."
        )
        @Null(message = "필수 자격증 파일은 수정할 수 없습니다.")
        Long certificateFileId,

        @Schema(
                description = "수정할 자격증 목록입니다. 필수 자격증 파일 자체는 수정할 수 없습니다.",
                example = "[\"NSCA-CPT\", \"ACSM 인증 트레이너\"]"
        )
        @Size(max = 30, message = "자격증은 최대 30개까지 입력할 수 있습니다.")
        List<String> qualifications,

        @Schema(
                description = "수정할 수상/대회경력 목록입니다.",
                example = "[\"2023 피지크 대회 입상\"]"
        )
        @Size(max = 100, message = "수상/대회경력은 최대 100개까지 입력할 수 있습니다.")
        List<String> awardHistories,

        @Schema(description = "자기소개", example = "안녕하세요. 체형 교정 전문 트레이너입니다.")
        @NotBlank(message = "자기소개는 필수입니다.")
        @Size(max = 1000, message = "자기소개는 최대 1000자까지 입력할 수 있습니다.")
        String introduction
) {
}
