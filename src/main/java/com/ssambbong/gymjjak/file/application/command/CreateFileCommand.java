package com.ssambbong.gymjjak.file.application.command;

import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;

public record CreateFileCommand(
        Long uploaderId,
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize,
        FileType fileType
) {
    public CreateFileCommand {
        if (uploaderId == null) {
            throw new InvalidFileException(
                    FileErrorCode.FILE_INVALID_REQUEST, "업로더 ID는 필수입니다.");
        }
        if (fileKey == null || fileKey.isBlank()) {
            throw new InvalidFileException(
                    FileErrorCode.FILE_INVALID_REQUEST, "파일 키는 필수입니다.");
        }
        if (fileType == null) {
            throw new InvalidFileException(
                    FileErrorCode.FILE_INVALID_REQUEST, "파일 타입은 필수입니다.");
        }
    }
}
