package com.ssambbong.gymjjak.pt.feedback.application.port;

import java.util.List;

// 피드백 목록 응답은 '커리큘럼' 기준으로 구성됨

public interface PtCurriculumQueryPort {

    List<CurriculumSummary> findAllByPtCourseId(Long ptCourseId);

    record CurriculumSummary(
            Long ptCurriculumId,
            int sessionNo,
            String title
    ) {}
}
