package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

/* Comment
*   파일 타입은 미포함.
*   Service 계층에서 프로필인지, 필수 자격증인지 검증
* */
public record UploadedFileMetadataCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {
    public UploadedFileMetadataCommand {
        if (fileKey == null || fileKey.isBlank()) {
            throw new InvalidTrainerApplicationException("fileKey는 필수입니다.");
        }

        if (originalName == null || originalName.isBlank()) {
            throw new InvalidTrainerApplicationException("originalName은 필수입니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new InvalidTrainerApplicationException("contentType은 필수입니다.");
        }

        if (fileSize == null || fileSize <= 0) {
            throw new InvalidTrainerApplicationException("fileSize는 1 이상이어야 합니다.");
        }

        fileKey = fileKey.trim();
        originalName = originalName.trim();
        contentType = contentType.trim();
    }
}
