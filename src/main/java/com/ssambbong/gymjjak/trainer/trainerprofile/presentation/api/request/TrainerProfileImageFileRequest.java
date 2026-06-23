package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TrainerProfileImageFileRequest(

        @Schema(
                description = "Presigned URL 발급 시 반환받은 S3 객체 Key"
        )
        @NotBlank(message = "fileKey는 필수입니다.")
        @Size(
                max = 500,
                message = "fileKey는 최대 500자까지 입력할 수 있습니다."
        )
        String fileKey,

        @Schema(
                description = "사용자가 업로드한 원본 파일명",
                example = "trainer-profile.png"
        )
        @NotBlank(message = "originalName은 필수입니다.")
        @Size(
                max = 255,
                message = "originalName은 최대 255자까지 입력할 수 있습니다."
        )
        String originalName,

        @Schema(
                description = "파일 MIME 타입",
                example = "image/png"
        )
        @NotBlank(message = "contentType은 필수입니다.")
        @Size(
                max = 100,
                message = "contentType은 최대 100자까지 입력할 수 있습니다."
        )
        String contentType,

        @Schema(
                description = "파일 크기(byte)",
                example = "524288"
        )
        @NotNull(message = "fileSize는 필수입니다.")
        @Positive(message = "fileSize는 1 이상이어야 합니다.")
        Long fileSize
) {
}
