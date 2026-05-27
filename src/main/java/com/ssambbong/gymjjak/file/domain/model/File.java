package com.ssambbong.gymjjak.file.domain.model;

import com.ssambbong.gymjjak.file.domain.policy.FilePolicy;
import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.Getter;

@Getter
public class File {

    private final Long fileId;
    private final Long uploaderId;
    private final String originalName;
    private final String storedName;
    private final String fileUrl;
    private final String contentType;
    private final Long fileSize;
    private final FileType fileType;

    private File(
            Long fileId,
            Long uploaderId,
            String originalName,
            String storedName,
            String fileUrl,
            String contentType,
            Long fileSize,
            FileType fileType
    ) {
        this.fileId = fileId;
        this.uploaderId = uploaderId;
        this.originalName = originalName;
        this.storedName = storedName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public static File create(
            Long uploaderId,
            String originalName,
            String storedName,
            String fileUrl,
            String contentType,
            Long fileSize,
            FileType fileType
    ) {
        File file = new File(
                null,
                uploaderId,
                originalName,
                storedName,
                fileUrl,
                contentType,
                fileSize,
                fileType
        );
        file.validate();
        return file;
    }

    // DB 조회 시 사용 → validate() 실행 안 함
    public static File restore(
            Long fileId,
            Long uploaderId,
            String originalName,
            String storedName,
            String fileUrl,
            String contentType,
            Long fileSize,
            FileType fileType
    ) {
        return new File(
                fileId,
                uploaderId,
                originalName,
                storedName,
                fileUrl,
                contentType,
                fileSize,
                fileType
        );
    }

    private void validate() {
        if (fileType == null) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_REQUEST, "파일 타입은 필수입니다.");
        }
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_REQUEST, "파일명은 필수입니다.");
        }
        FilePolicy policy = FilePolicy.from(fileType);

        if (contentType == null || !policy.isAllowed(contentType)) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_TYPE);
        }
        if (fileSize == null || fileSize <= 0 || !policy.isAllowedSize(fileSize)) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_SIZE);
        }
    }
}
