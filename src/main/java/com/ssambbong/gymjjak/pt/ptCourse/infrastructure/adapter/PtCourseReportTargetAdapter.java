package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.ptCourse.application.port.TrainerProfileQueryPort;
import com.ssambbong.gymjjak.pt.ptCourse.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.report.application.port.ptcourse.PtCourseReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtCourseReportTargetAdapter implements PtCourseReportTargetPort {

    private final PtCourseRepository ptCourseRepository;

    private final TrainerProfileQueryPort trainerProfileQueryPort;

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.debug("[PtCourseSnapshot] ptCourseId={}", targetId);

        PtCourse ptCourse = ptCourseRepository.findById(targetId)
                .orElseThrow(PtCourseNotFoundException::new);

        Long targetOwnerId = trainerProfileQueryPort.findUserIdByTrainerProfileId(
                ptCourse.getTrainerProfileId()
        );

        log.info("[PtCourseSnapshot] ptCourseId={}, title={}", targetId, ptCourse.getTitle());

        return new ReportTargetSnapshot(
                ptCourse.getId(),
                targetOwnerId,                  // PT 강습 소유자 = 트레이너
                ptCourse.getTitle(),            // 제목
                ptCourse.getDescription(),      // 내용
                null                            // 파일은 null로 넘기기
        );
    }
}
