package com.ssambbong.gymjjak.file.presentation.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeneratePresignedUrlsRequest(
        @NotEmpty(message = "파일 항목이 필요합니다.")
        @Size(max = 10, message = "한 번에 최대 10개까지 요청할 수 있습니다.")
        List<@NotNull(message = "파일 항목은 null일 수 없습니다.") @Valid GeneratePresignedUrlRequest> files
) {}
