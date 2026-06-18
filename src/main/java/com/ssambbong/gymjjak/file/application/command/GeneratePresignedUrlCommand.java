package com.ssambbong.gymjjak.file.application.command;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;

public record GeneratePresignedUrlCommand(
        Long uploaderId,
        FileType fileType,
        String contentType
) {
}
