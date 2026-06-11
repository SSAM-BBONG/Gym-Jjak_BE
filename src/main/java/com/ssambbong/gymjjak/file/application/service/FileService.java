package com.ssambbong.gymjjak.file.application.service;

import com.ssambbong.gymjjak.file.application.command.FileUploadCommand;
import com.ssambbong.gymjjak.file.application.port.FileMetricsPort;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.policy.FilePolicy;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import com.ssambbong.gymjjak.global.infrastructure.aop.Monitored;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileRepository fileRepository;
    private final FileMetricsPort fileMetricsPort;

    @Override
    public PresignedUrlResult generatePresignedUploadUrl(Long uploaderId, FileType fileType, String contentType, String originalName) {
        FilePolicy policy = FilePolicy.from(fileType);
        if (!policy.isAllowed(contentType)) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_TYPE);
        }

        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1)
                : null;
        String key = ext != null
                ? String.format("%s/%d/%s.%s", fileType.getPath(), uploaderId, UUID.randomUUID(), ext)
                : String.format("%s/%d/%s", fileType.getPath(), uploaderId, UUID.randomUUID());

        String presignedUrl = fileStoragePort.generatePresignedUploadUrl(key, contentType);
        log.info("Presigned URL 발급 - uploaderId: {}, fileType: {}, key: {}", uploaderId, fileType, key);
        fileMetricsPort.recordPresignedUrlGenerated();
        return new PresignedUrlResult(presignedUrl, key);
    }

    @Override
    @Transactional
    public Long registerFile(FileUploadCommand command) {
        String expectedKeySegment = "/" + command.uploaderId() + "/";
        if (!command.fileKey().contains(expectedKeySegment)) {
            throw new FileAccessDeniedException();
        }

        String storedName = command.fileKey().substring(command.fileKey().lastIndexOf('/') + 1);
        File file = File.create(
                command.uploaderId(),
                command.originalName(),
                storedName,
                command.fileKey(),
                command.contentType(),
                command.fileSize(),
                command.fileType()
        );
        Long fileId = fileRepository.save(file).getFileId();
        log.info("파일 등록 완료 - fileId: {}, key: {}", fileId, command.fileKey());
        fileMetricsPort.recordFileRegistered();
        return fileId;
    }

    @Override
    @Transactional(readOnly = true)
    public String getPresignedUrl(Long fileId, Long requesterId, boolean isAdmin) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        if (FilePolicy.from(file.getFileType()).isRequiresOwnershipCheck()
                && !isAdmin
                && !file.getUploaderId().equals(requesterId)) {
            throw new FileAccessDeniedException();
        }
        return fileStoragePort.getPresignedUrl(file.getFileUrl());
    }

    @Monitored(name = "gymjjak.file.delete.duration", description = "S3 파일 삭제 소요 시간", domain = "file", action = "delete")
    @Override
    @Transactional
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));
        fileRepository.deleteById(fileId);
        fileStoragePort.delete(file.getFileUrl());
        log.info("파일 삭제 완료 - fileId: {}, key: {}", fileId, file.getFileUrl());
        fileMetricsPort.recordFileDeleted();
    }
}
