package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.file.application.usecase.FileUseCase;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtCourseCommandService implements PtCourseCommandUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final FileUseCase fileUseCase;
    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public Long createPtCourse(MultipartFile thumbnail, CreatePtCourseCommand command) {

        log.debug("[PtCourseCreate] categoryId={}, tagId={}, price={}, totalSessionCount={}",
                command.categoryId(), command.tagId(), command.price(), command.totalSessionCount());

        // userId로 trainerProfileId, organizationId 조회
        TrainerProfileQueryPort.TrainerInfo trainerInfo =
                trainerProfileQueryPort.findByUserId(command.userId());

        // 썸네일 파일 업로드
        Long thumbnailFileId = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailFileId = fileUseCase.uploadFile(thumbnail, command.userId(), FileType.PT_THUMBNAIL);
        }

        // 도메인 객체 생성
        PtCourse ptCourse = PtCourse.create(
                trainerInfo.organizationId(),
                trainerInfo.trainerProfileId(),
                command.categoryId(),
                command.tagId(),
                thumbnailFileId,
                command.title(),
                command.description(),
                command.price(),
                command.totalSessionCount()
        );

        // 저장 후 id 반환
        PtCourse saved = ptCourseRepository.save(ptCourse);
        log.info("[PtCourseCreate] ptCourseId={}", saved.getId());
        return saved.getId();
    }
}
