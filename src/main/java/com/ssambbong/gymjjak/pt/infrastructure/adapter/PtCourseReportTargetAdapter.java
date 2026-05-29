package com.ssambbong.gymjjak.pt.infrastructure.adapter;

import com.ssambbong.gymjjak.pt.domain.exception.PtCourseNotFoundException;
import com.ssambbong.gymjjak.pt.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.domain.repository.PtCourseRepository;
import com.ssambbong.gymjjak.report.application.port.PtCourseReportTargetPort;
import com.ssambbong.gymjjak.report.application.port.ReportTargetSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtCourseReportTargetAdapter implements PtCourseReportTargetPort {

    private final PtCourseRepository ptCourseRepository;

    @Override
    public ReportTargetSnapshot getSnapshot(Long targetId) {
        log.debug("[PtCourseSnapshot] ptCourseId={}", targetId);

        PtCourse ptCourse = ptCourseRepository.findById(targetId)
                .orElseThrow(PtCourseNotFoundException::new);

        log.info("[PtCourseSnapshot] ptCourseId={}, title={}", targetId, ptCourse.getTitle());

        return new ReportTargetSnapshot(
                ptCourse.getId(),
                ptCourse.getTrainerProfileId(), // PT 강습 소유자 = 트레이너
                ptCourse.getTitle(),            // 제목
                ptCourse.getDescription(),      // 내용
                null                            // 파일은 null로 넘기기
        );
    }
}
