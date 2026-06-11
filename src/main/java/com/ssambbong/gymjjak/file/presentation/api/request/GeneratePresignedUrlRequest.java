package com.ssambbong.gymjjak.file.presentation.api.request;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GeneratePresignedUrlRequest(
        @NotNull(message = "파일 타입은 필수입니다.")
        FileType fileType,

        @NotBlank(message = "Content-Type은 필수입니다.")
        String contentType,

        @NotBlank(message = "파일명은 필수입니다.")
        String originalName
) {}
