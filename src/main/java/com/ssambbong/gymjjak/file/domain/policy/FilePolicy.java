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
 * FilePolicy(file 도메인)는 업로드 정책(허용 MIME, 최대 크기) 관리
 */
@Getter
@RequiredArgsConstructor
public enum FilePolicy {

    // 이미지 정책: jpg, jpeg, png, webp 허용 / 최대 10MB
    PROFILE_IMAGE(
            FileType.PROFILE_IMAGE,
            List.of("image/jpeg", "image/png", "image/webp"),
            10 * 1024 * 1024L
    ),
    PT_THUMBNAIL(
            FileType.PT_THUMBNAIL,
            List.of("image/jpeg", "image/png", "image/webp"),
            10 * 1024 * 1024L
    ),
    CERTIFICATION(
            FileType.CERTIFICATION,
            List.of("image/jpeg", "image/png", "image/webp"),
            10 * 1024 * 1024L
    ),
    AWARD(
            FileType.AWARD,
            List.of("image/jpeg", "image/png", "image/webp"),
            10 * 1024 * 1024L
    ),
    BUSINESS_LICENSE(
            FileType.BUSINESS_LICENSE,
            List.of("image/jpeg", "image/png", "image/webp", "application/pdf"),
            10 * 1024 * 1024L
    ),

    // 영상 정책: mp4, mov 허용 / 최대 50MB
    FEEDBACK_VIDEO(
            FileType.FEEDBACK_VIDEO,
            List.of("video/mp4", "video/quicktime"),
            50 * 1024 * 1024L
    );

    private final FileType fileType;
    private final List<String> allowedMimeTypes;
    private final long maxFileSize;

    // FileType으로 FilePolicy 찾기
    public static FilePolicy from(FileType fileType) {
        return Arrays.stream(values())
                .filter(p -> p.fileType == fileType)
                .findFirst()
                .orElseThrow(() -> new InvalidFileException(
                        FileErrorCode.FILE_INVALID_TYPE));
    }

    // MIME 타입 허용 여부 검증
    public boolean isAllowed(String mimeType) {
        return allowedMimeTypes.contains(mimeType);
    }

    // 파일 크기 허용 여부 검증
    public boolean isAllowedSize(long fileSize) {
        return fileSize <= maxFileSize;
    }
}
