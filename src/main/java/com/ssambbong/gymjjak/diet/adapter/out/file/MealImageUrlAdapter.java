package com.ssambbong.gymjjak.diet.adapter.out.file;

import com.ssambbong.gymjjak.diet.application.port.out.MealImageUrlPort;
import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.file.domain.repository.FileStoragePort;
import com.ssambbong.gymjjak.file.exception.FileAccessDeniedException;
import com.ssambbong.gymjjak.file.exception.FileErrorCode;
import com.ssambbong.gymjjak.file.exception.FileNotFoundException;
import com.ssambbong.gymjjak.file.exception.InvalidFileException;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MealImageUrlAdapter implements MealImageUrlPort {

    private final FileRepository fileRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public String resolve(Long fileId, Long mealOwnerUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!mealOwnerUserId.equals(file.getUploaderId())) {
            throw new FileAccessDeniedException();
        }
        if (file.getFileType() != FileType.MEAL_IMAGE) {
            throw new InvalidFileException(FileErrorCode.FILE_TYPE_MISMATCH);
        }

        return fileStoragePort.getPresignedUrl(file.getFileUrl());
    }
}
