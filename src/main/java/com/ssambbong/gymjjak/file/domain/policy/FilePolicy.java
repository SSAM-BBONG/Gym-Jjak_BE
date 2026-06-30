package com.ssambbong.gymjjak.file.domain.policy;

import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 파일 업로드 정책
 * FileType(global)은 S3 경로만 관리
 * FilePolicy(file 도메인)는 업로드 정책(허용 MIME, 최대 크기, 접근 제어) 관리
 */
@Getter
@RequiredArgsConstructor
public enum FilePolicy {

    PROFILE_IMAGE(
            FileType.PROFILE_IMAGE,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L,
            false
    ),
    PT_THUMBNAIL(
            FileType.PT_THUMBNAIL,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L,
            false
    ),
    CERTIFICATION(
            FileType.CERTIFICATION,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L,
            true
    ),
    AWARD(
            FileType.AWARD,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L,
            true
    ),
    BUSINESS_LICENSE(
            FileType.BUSINESS_LICENSE,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L,
            true
    ),
    FEEDBACK_VIDEO(
            FileType.FEEDBACK_VIDEO,
            List.of("video/mp4", "video/quicktime"),
            50 * 1024 * 1024L,
            true
    );

    private final FileType fileType;
    private final List<String> allowedMimeTypes;
    private final long maxFileSize;
    private final boolean requiresOwnershipCheck;

    public static FilePolicy from(FileType fileType) {
        return Arrays.stream(values())
                .filter(p -> p.fileType == fileType)
                .findFirst()
                .orElseThrow(() -> new InvalidFileException(
                        FileErrorCode.FILE_INVALID_TYPE));
    }

    public boolean isAllowed(String mimeType) {
        return allowedMimeTypes.contains(mimeType);
    }

    public boolean isAllowedSize(long fileSize) {
        return fileSize <= maxFileSize;
    }
}
