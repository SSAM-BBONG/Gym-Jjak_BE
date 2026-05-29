package com.ssambbong.gymjjak.file.application.service;

import com.ssambbong.gymjjak.file.application.command.FileUploadCommand;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.file.exception.FileUploadException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public Long uploadFile(MultipartFile multipartFile, Long uploaderId, FileType fileType) {
        return saveFile(multipartFile, uploaderId, fileType);
    }

    @Override
    @Transactional
    public Long replaceFile(Long oldFileId, MultipartFile multipartFile,
                            Long uploaderId, FileType fileType) {
        // 1. 기존 파일 조회 → S3 key 확보
        File oldFile = fileRepository.findById(oldFileId)
                .orElseThrow(() -> new FileNotFoundException(oldFileId));

        // 2. 새 파일 저장 (S3 업로드 + DB 저장)
        Long newFileId = saveFile(multipartFile, uploaderId, fileType);

        // 3. 기존 S3 파일 삭제
        fileStoragePort.delete(oldFile.getFileUrl());

        // 4. 기존 DB soft delete
        fileRepository.deleteById(oldFileId);

        return newFileId;
    }

    @Override
    @Transactional(readOnly = true)
    public String getPresignedUrl(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        return fileStoragePort.getPresignedUrl(file.getFileUrl());
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) {
        boolean deleted = fileRepository.deleteById(fileId);
        if (!deleted) {
            throw new FileNotFoundException(fileId);
        }
    }

    @Override
    @Transactional
    public void deleteFromStorage(Long fileId) {
        // REQUIRED: 외부 트랜잭션에 합류해 1차 캐시에서 엔티티를 조회
        // (REQUIRES_NEW 사용 시 외부 트랜잭션의 미커밋 데이터를 볼 수 없어 FileNotFoundException 발생)
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        fileStoragePort.delete(file.getFileUrl());
    }

    // uploadFile, replaceFile 공통 로직
    private Long saveFile(MultipartFile multipartFile, Long uploaderId, FileType fileType) {
        // 1. Command 조립 (생성자에서 null 검증 자동 실행)
        FileUploadCommand command = new FileUploadCommand(
                uploaderId,
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                multipartFile.getSize(),
                fileType
        );

        // 2. S3 업로드 → key 반환
        String key = fileStoragePort.upload(multipartFile, command.fileType(), command.uploaderId());

        // 3. 도메인 객체 생성
        String storedName = key.substring(key.lastIndexOf("/") + 1);
        File file = File.create(
                command.uploaderId(),
                command.originalName(),
                storedName,
                key,
                command.contentType(),
                command.fileSize(),
                command.fileType()
        );

        // 4. DB 저장 → 실패 시 S3 파일 롤백
        try {
            return fileRepository.save(file).getFileId();
        } catch (DataAccessException e) {
            log.error("DB 저장 실패 → S3 파일 롤백 - key: {}", key);
            fileStoragePort.delete(key);
            throw new FileUploadException(e);
        }
    }
}
