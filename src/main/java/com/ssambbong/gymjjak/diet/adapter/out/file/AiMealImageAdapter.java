package com.ssambbong.gymjjak.diet.adapter.out.file;

import com.ssambbong.gymjjak.diet.application.port.out.AiMealImagePort;
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
public class AiMealImageAdapter implements AiMealImagePort {
    private final FileRepository fileRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public String resolveAccessibleImageUrl(Long fileId, Long userId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // AI 분석에는 인증 사용자가 직접 업로드한 파일만 사용할 수 있다.
        if (!userId.equals(file.getUploaderId())) {
            throw new FileAccessDeniedException();
        }
        // 다른 기능에서 업로드한 이미지가 식단 분석에 재사용되지 않도록 식단 이미지 유형까지 확인한다.
        if (file.getFileType() != FileType.MEAL_IMAGE
                || file.getContentType() == null
                || !file.getContentType().startsWith("image/")) {
            throw new InvalidFileException(FileErrorCode.FILE_INVALID_TYPE);
        }

        // AI 서버가 제한된 시간 동안 원본 이미지를 읽을 수 있도록 GET Presigned URL을 발급한다.
        return fileStoragePort.getPresignedUrl(file.getFileUrl());
    }
}
