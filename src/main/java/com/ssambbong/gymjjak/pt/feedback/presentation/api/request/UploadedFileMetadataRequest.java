package com.ssambbong.gymjjak.pt.feedback.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// 프론트가 전달하는 파일 메타데이터 요청 DTO
public record UploadedFileMetadataRequest(

        @Schema(
                description = "Presigned URL 발급 응답으로 받은 S3 객체 key",
                example = "uploads/feedbacks/videos/1/uuid.mp4"
        )
        @NotBlank(message = "fileKey는 필수입니다.")
        @Size(max = 500)
        String fileKey,

        @Schema(description = "사용자가 업로드한 원본 파일명", example = "before.mp4")
        @NotBlank(message = "originalName은 필수입니다.")
        @Size(max = 255)
        String originalName,

        @Schema(description = "파일 MIME 타입", example = "video/mp4")
        @NotBlank(message = "contentType은 필수입니다.")
        @Size(max = 100)
        String contentType,

        @Schema(description = "파일 크기(byte)", example = "10485760")
        @NotNull(message = "fileSize는 필수입니다.")
        @Positive(message = "fileSize는 1 이상이어야 합니다.")
        Long fileSize
) {
}
