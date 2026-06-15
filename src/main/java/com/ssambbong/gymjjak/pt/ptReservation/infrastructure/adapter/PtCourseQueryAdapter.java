package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCurriculumRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PtCourseQueryAdapter implements PtCourseQueryPort {

    private final PtCourseRepository ptCourseRepository;
    private final PtCurriculumRepository ptCurriculumRepository;
    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public PtCourseInfo findPtCourseInfo(Long ptCourseId) {
        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        String trainerName = trainerProfileQueryPort
                .findSummaryById(ptCourse.getTrainerProfileId())
                .trainerName();

        return new PtCourseInfo(ptCourse.getTitle(), ptCourse.getThumbnailFileId(), trainerName);
    }

    @Override
    public List<CurriculumInfo> findCurriculumsByPtCourseId(Long ptCourseId) {
        return ptCurriculumRepository.findAllByPtCourseId(ptCourseId).stream()
                .map(c -> new CurriculumInfo(c.getId(), c.getSessionNo(), c.getTitle()))
                .toList();
    }
}
