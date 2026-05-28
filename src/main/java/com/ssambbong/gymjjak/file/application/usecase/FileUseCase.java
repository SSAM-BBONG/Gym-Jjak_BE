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

    // 파일 삭제
    void deleteFile(Long fileId);
}
