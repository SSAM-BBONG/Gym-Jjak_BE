package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTrainerApplicationRequest(

        // 신청 대상 조직 ID
        // 조직 검색 API에서 선택한 organizationId 전달
        @Schema(description = "트레이너 신청서를 제출할 조직 ID 목록",
                example = "[1, 2, 3]")
        @NotNull(message = "신청 대상 조직은 하나 이상 선택해야 합니다.")
        List<
                @NotNull(message = "조직 ID는 null일 수 없습니다.")
                @Positive(message = "조직 ID는 1 이상이어야 합니다.")
                Long> organizationIds,

        @Schema(description = "S3 업로드가 완료된 프로필 이미지 메타데이터")
        @Valid
        UploadedFileMetadataRequest profileImageFile,

        @Schema(description = "S3 업로드가 완료된 필수 자격증 파일 메타데이터")
        @NotNull(message = "필수 자격증 파일은 필수입니다.")
        @Valid
        UploadedFileMetadataRequest certificateFile,

        @Schema(
                description = "사용자가 직접 입력한 자격증 목록입니다. 필수 자격증 검증은 이 값이 아니라 OCR 결과를 기준으로 처리합니다." +
                        "null이면 빈 목록으로 처리됩니다.",
                example = "[\"생활스포츠지도사 2급\", \"NSCA-CPT\"]"
        )
        @Size(max = 30, message = "자격증은 최대 30개까지 입력할 수 있습니다.")
        List<String> qualifications,

        @Schema(
                description = "사용자가 직접 입력한 수상/대회경력 목록입니다." +
                        "null이면 빈 목록으로 처리됩니다.",
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
