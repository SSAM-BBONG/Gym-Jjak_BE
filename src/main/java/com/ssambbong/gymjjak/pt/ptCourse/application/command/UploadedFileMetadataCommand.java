package com.ssambbong.gymjjak.pt.ptCourse.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseInvalidException;

/**
 * S3 업로드 완료 후 전달받은 파일 메타데이터 커맨드.
 * 파일 타입(FileType)은 서비스 계층에서 결정하므로 포함하지 않는다.
 */
public record UploadedFileMetadataCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {
    public UploadedFileMetadataCommand {
        if (fileKey == null || fileKey.isBlank()) throw new PtCourseInvalidException();
        if (originalName == null || originalName.isBlank()) throw new PtCourseInvalidException();
        if (contentType == null || contentType.isBlank()) throw new PtCourseInvalidException();
        if (fileSize == null || fileSize <= 0) throw new PtCourseInvalidException();
        fileKey = fileKey.trim();
        originalName = originalName.trim();
        contentType = contentType.trim();
    }
}
