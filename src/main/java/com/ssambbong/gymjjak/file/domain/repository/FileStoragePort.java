package com.ssambbong.gymjjak.file.domain.repository;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * S3 파일 저장소 포트 (인터페이스)
 * domain은 S3가 뭔지 모름 - infrastructure에서 구현
 */
public interface FileStoragePort {

    // 파일 업로드 → S3 key 반환
    String upload(MultipartFile file, FileType fileType, Long uploaderId);

    // S3 key로 Presigned URL 발급
    String getPresignedUrl(String key);

    // 파일 삭제
    void delete(String key);
}
