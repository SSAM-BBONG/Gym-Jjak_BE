package com.ssambbong.gymjjak.pt.ptCourse.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UploadedFileMetadataRequest(
        @Schema(
                description = "Presigned URL 발급 응답으로 받은 S3 객체 key",
                example = "uploads/courses/thumbnails/1/uuid.jpg"
        )
        @NotBlank(message = "fileKey는 필수입니다.")
        @Size(max = 500)
        String fileKey,

        @Schema(description = "사용자가 업로드한 원본 파일명", example = "thumbnail.jpg")
        @NotBlank(message = "originalName은 필수입니다.")
        @Size(max = 255)
        String originalName,

        @Schema(description = "파일 MIME 타입", example = "image/jpeg")
        @NotBlank(message = "contentType은 필수입니다.")
        @Size(max = 100)
        String contentType,

        @Schema(description = "파일 크기(byte)", example = "524288")
        @NotNull(message = "fileSize는 필수입니다.")
        @Positive(message = "fileSize는 1 이상이어야 합니다.")
        Long fileSize
) {}

