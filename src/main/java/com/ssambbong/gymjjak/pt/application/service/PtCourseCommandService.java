package com.ssambbong.gymjjak.pt.application.service;

import com.ssambbong.gymjjak.pt.application.command.CreatePtCourseCommand;
import com.ssambbong.gymjjak.pt.application.usecase.PtCourseCommandUseCase;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PtCourseCommandService implements PtCourseCommandUseCase {

    private final PtCourseRepository ptCourseRepository;

    @Override
    public Long createPtCourse(CreatePtCourseCommand command) {
        // 도메인 객체 생성
        PtCourse ptCourse = PtCourse.create(
                command.organizationId(),
                command.trainerProfileId(),
                command.categoryId(),
                command.tagId(),
                command.thumbnailFileId(),
                command.title(),
                command.description(),
                command.price(),
                command.totalSessionCount()
        );
        // 저장 후 id 반환
        PtCourse saved = ptCourseRepository.save(ptCourse);
        return saved.getId();
    }
}
