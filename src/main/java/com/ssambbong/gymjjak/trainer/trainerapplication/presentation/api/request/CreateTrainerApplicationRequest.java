package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTrainerApplicationRequest(

        @Schema(description = "프로필 이미지 파일 ID" +
                "프론트가 PROFILE_IMAGE 타입으로 파일 업로드/등록을 끝낸 뒤 받은 fileId를 전달합니다." +
                "예: uploads/profiles/trainers/{userId}/{uuid}.png 에 해당하는 files.file_id")
        Long profileImageFileId,

        @Schema(description = "자격증 파일 ID" +
                "프론트가 CERTIFICATION 타입으로 파일 업로드/등록을 끝낸 뒤 받은 fileId를 전달합니다." +
                "이 파일은 백엔드에서 S3 bytes를 읽어 OCR 검증에 사용합니다.")
        @NotNull(message = "자격증 파일은 필수입니다.")
        Long certificateFileId,

        @Schema(
                description = "사용자가 직접 입력한 자격증 목록입니다. 필수 자격증 검증은 이 값이 아니라 OCR 결과를 기준으로 처리합니다.",
                example = "[\"생활스포츠지도사 2급\", \"NSCA-CPT\"]"
        )
        @Size(max = 30, message = "자격증은 최대 30개까지 입력할 수 있습니다.")
        List<String> qualifications,

        @Schema(
                description = "사용자가 직접 입력한 수상/대회경력 목록입니다.",
                example = "[\"2023 피지크 대회 입상\"]"
        )
        @Size(max = 100, message = "수상 경력은 최대 100개까지 입력할 수 있습니다.")
        List<String> awardHistories,

        @Schema(description = "자기소개", example = "안녕하세요. 체형 교정 전문 트레이너입니다.")
        @NotBlank(message = "자기소개는 필수입니다.")
        @Size(max = 1000, message = "자기소개는 최대 1000자까지 입력할 수 있습니다.")
        String introduction
) {
}
