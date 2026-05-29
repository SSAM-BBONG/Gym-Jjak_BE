package com.ssambbong.gymjjak.file.application.usecase;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileUseCase {

    // 파일 업로드 → file_id 반환
    Long uploadFile(MultipartFile multipartFile, Long uploaderId, FileType fileType);

    // 파일 교체 → 새 file_id 반환
    // 기존 파일 삭제 후 새 파일 업로드
    Long replaceFile(Long oldFileId, MultipartFile multipartFile, Long uploaderId, FileType fileType);

    // S3 key로 Presigned URL 발급
    String getPresignedUrl(Long fileId);

    // 파일 삭제 (DB soft delete)
    void deleteFile(Long fileId);

    // S3 스토리지에서만 삭제 (DB는 건드리지 않음 — 트랜잭션 롤백으로 처리되는 경우에 사용)
    void deleteFromStorage(Long fileId);
}
