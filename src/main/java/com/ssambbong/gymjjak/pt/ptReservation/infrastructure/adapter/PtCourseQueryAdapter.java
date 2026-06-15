package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.pt.ptReservation.application.port.PtCourseQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PtCourseQueryAdapter implements PtCourseQueryPort {

    private final PtCourseRepository ptCourseRepository;

    @Override
    public PtCourseInfo findPtCourseInfo(Long ptCourseId) {
        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        return new PtCourseInfo(ptCourse.getTitle(), ptCourse.getThumbnailFileId());
    }
}
