package com.ssambbong.gymjjak.file.application.usecase;

import com.ssambbong.gymjjak.file.application.command.CreateFileCommand;
import com.ssambbong.gymjjak.file.application.command.GeneratePresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.command.GetPresignedUrlCommand;
import com.ssambbong.gymjjak.file.application.result.FileContentResult;
import com.ssambbong.gymjjak.file.application.result.FileRegistrationResult;
import com.ssambbong.gymjjak.file.application.result.PresignedUrlResult;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;

import java.util.List;

public interface FileUseCase {

    List<PresignedUrlResult> generatePresignedUploadUrls(List<GeneratePresignedUrlCommand> commands);

    List<FileRegistrationResult> registerFiles(List<CreateFileCommand> commands);

    String getPresignedUrl(GetPresignedUrlCommand command);

    FileContentResult downloadFile(Long fileId, Long requesterId, boolean isAdmin, FileType expectedFileType);

    void deleteFile(Long fileId);
}
