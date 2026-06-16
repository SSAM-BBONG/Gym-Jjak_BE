package com.ssambbong.gymjjak.file.presentation.api.request;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterFileRequest(
        @NotBlank(message = "파일 키는 필수입니다.")
        String fileKey,

        @NotBlank(message = "파일명은 필수입니다.")
        String originalName,

        @NotBlank(message = "Content-Type은 필수입니다.")
        String contentType,

        @NotNull(message = "파일 크기는 필수입니다.")
        @Positive(message = "파일 크기는 0보다 커야 합니다.")
        Long fileSize,

        @NotNull(message = "파일 타입은 필수입니다.")
        FileType fileType
) {}
