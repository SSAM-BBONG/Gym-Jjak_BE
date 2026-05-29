package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.report.application.port.PtCourseSanctionPort;
import com.ssambbong.gymjjak.report.application.port.ReportSanctionAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtCourseSanctionAdapter implements PtCourseSanctionPort {

    private final PtCourseRepository ptCourseRepository;

    @Override
    @Transactional
    public void changeAutoBlind(Long ptCourseId, ReportSanctionAction action) {
        log.debug("[PtCourseBlind] ptCourseId={}, action={}", ptCourseId, action);

        PtCourse ptCourse = ptCourseRepository.findById(ptCourseId)
                .orElseThrow(PtCourseNotFoundException::new);

        switch (action) {
            case APPLY_AUTO_BLIND -> ptCourse.blind();
            case RELEASE_AUTO_BLIND -> ptCourse.unblind();
        }

        ptCourseRepository.save(ptCourse);

        log.info("[PtCourseBlind] ptCourseId={}, action={}, status={}",
                ptCourseId, action, ptCourse.getStatus());
    }
}
