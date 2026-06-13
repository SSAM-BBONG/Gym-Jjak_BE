package com.ssambbong.gymjjak.file.application.usecase;

import com.ssambbong.gymjjak.file.application.command.FileUploadCommand;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;

public interface FileUseCase {

    PresignedUrlResult generatePresignedUploadUrl(Long uploaderId, FileType fileType, String contentType, String originalName);

    Long registerFile(FileUploadCommand command);

    String getPresignedUrl(Long fileId, Long requesterId, boolean isAdmin);

    void deleteFile(Long fileId);
}
