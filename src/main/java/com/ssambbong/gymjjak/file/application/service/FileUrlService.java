package com.ssambbong.gymjjak.file.application.service;

import com.ssambbong.gymjjak.file.application.command.GetPresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.usecase.FileUrlUseCase;
import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.policy.FilePolicy;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileUrlService implements FileUrlUseCase {

    private final FileRepository fileRepository;
    private final FileStoragePort fileStoragePort;
    private final FileUseCase fileUseCase;

    @Override
    @Transactional(readOnly = true)
    public String getUrl(Long fileId, Long requesterId, boolean isAdmin) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        FilePolicy policy = FilePolicy.from(file.getFileType());

        if (policy.isRequiresOwnershipCheck()) {
            return fileUseCase.getPresignedUrl(new GetPresignedUrlCommand(fileId, requesterId, isAdmin));
        }
        return fileStoragePort.getPublicUrl(file.getFileUrl());
    }
}
