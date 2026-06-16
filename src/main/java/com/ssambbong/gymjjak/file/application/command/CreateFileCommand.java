package com.ssambbong.gymjjak.file.application.command;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;

public record CreateFileCommand(
        Long uploaderId,
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize,
        FileType fileType
) {
}
