package com.ssambbong.gymjjak.file.application.service;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.command.GeneratePresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.command.GetPresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.port.FileMetricsPort;
import com.ssambbong.gymjjak.file.application.result.FileContentResult;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileRepository fileRepository;
    private final FileMetricsPort fileMetricsPort;

    @Override
    public List<PresignedUrlResult> generatePresignedUploadUrls(List<GeneratePresignedUrlCommand> commands) {
        return commands.stream()
                .map(this::doGeneratePresignedUploadUrl)
                .toList();
    }

    private PresignedUrlResult doGeneratePresignedUploadUrl(GeneratePresignedUrlCommand command) {
        FilePolicy policy = FilePolicy.from(command.fileType());
        if (!policy.isAllowed(command.contentType())) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_TYPE);
        }

        String key = String.format("%s/%d/%s", command.fileType().getPath(), command.uploaderId(), UUID.randomUUID());

        String presignedUrl = fileStoragePort.generatePresignedUploadUrl(key, command.contentType());
        log.info("Presigned URL 발급 - uploaderId: {}, fileType: {}, key: {}", command.uploaderId(), command.fileType(), key);
        recordMetricSafely(fileMetricsPort::recordPresignedUrlGenerated, "recordPresignedUrlGenerated");
        return new PresignedUrlResult(presignedUrl, key);
    }

    @Override
    @Transactional
    public List<FileRegistrationResult> registerFiles(List<CreateFileCommand> commands) {
        return commands.stream()
                .map(this::doRegisterFile)
                .toList();
    }

    private FileRegistrationResult doRegisterFile(CreateFileCommand command) {
        String expectedPrefix = command.fileType().getPath() + "/" + command.uploaderId() + "/";
        if (!command.fileKey().startsWith(expectedPrefix)) {
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
        recordMetricSafely(fileMetricsPort::recordFileRegistered, "recordFileRegistered");
        return new FileRegistrationResult(fileId, command.fileType());
    }

    @Override
    @Transactional(readOnly = true)
    public String getPresignedUrl(GetPresignedUrlCommand command) {
        File file = fileRepository.findById(command.fileId())
                .orElseThrow(() -> new FileNotFoundException(command.fileId()));

        FilePolicy policy = FilePolicy.from(file.getFileType());

        if (!policy.isRequiresOwnershipCheck()) {
            return fileStoragePort.getPublicUrl(file.getFileUrl());
        }

        if (!command.isAdmin() && !file.getUploaderId().equals(command.requesterId())) {
            throw new FileAccessDeniedException();
        }
        return fileStoragePort.getPresignedUrl(file.getFileUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public FileContentResult downloadFile(Long fileId, Long requesterId, boolean isAdmin, FileType expectedFileType) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!isAdmin && !file.getUploaderId().equals(requesterId)) {
            throw new FileAccessDeniedException();
        }

        if (file.getFileType() != expectedFileType) {
            throw new InvalidFileException(FileErrorCode.FILE_TYPE_MISMATCH);
        }

        byte[] bytes = fileStoragePort.download(file.getFileUrl());
        log.info("파일 다운로드 완료 - fileId: {}, key: {}", fileId, file.getFileUrl());
        return new FileContentResult(
                file.getOriginalName(),
                file.getContentType(),
                file.getFileSize(),
                file.getFileType(),
                bytes
        );
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
        recordMetricSafely(fileMetricsPort::recordFileDeleted, "recordFileDeleted");
    }

    private void recordMetricSafely(Runnable metricCall, String metricName) {
        try {
            metricCall.run();
        } catch (Exception e) {
            log.warn("메트릭 기록 실패 - metric: {}", metricName, e);
        }
    }
}
