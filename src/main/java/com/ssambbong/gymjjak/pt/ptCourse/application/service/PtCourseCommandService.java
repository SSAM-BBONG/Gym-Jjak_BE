package com.ssambbong.gymjjak.pt.ptCourse.application.service;

import com.ssambbong.gymjjak.pt.ptCourse.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.ptCourse.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PtCourseCommandService implements PtCourseCommandUseCase {

    private final PtCourseRepository ptCourseRepository;
    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public Long createPtCourse(CreatePtCourseCommand command) {

        log.debug("[PtCourseCreate] categoryId={}, tagId={}, price={}, curriculumCount={}",
                command.categoryId(), command.tagId(), command.price(), command.curriculums().size());

        // userId로 trainerProfileId, organizationId 조회
        TrainerProfileQueryPort.TrainerInfo trainerInfo =
                trainerProfileQueryPort.findByUserId(command.userId());

        // 도메인 객체 생성 (totalSessionCount = curriculums.size())
        PtCourse ptCourse = PtCourse.create(
                trainerInfo.organizationId(),
                trainerInfo.trainerProfileId(),
                command.categoryId(),
                command.tagId(),
                command.thumbnailFileId(),
                command.title(),
                command.description(),
                command.price(),
                command.curriculums().size()
        );

        // 저장 후 id 반환
        PtCourse saved = ptCourseRepository.save(ptCourse);
        log.info("[PtCourseCreate] ptCourseId={}", saved.getId());
        return saved.getId();
    }
}
