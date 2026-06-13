package com.ssambbong.gymjjak.file.application.result;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;

public record FileContentResult(
        String originalName,
        String contentType,
        Long fileSize,
        FileType fileType,
        byte[] bytes
) {
}
