package com.ssambbong.gymjjak.pt.feedback.application.port;

import java.util.List;

// 피드백 목록 응답은 '커리큘럼' 기준으로 구성됨

public interface PtCurriculumQueryPort {

    // 코스 ID로 커리큘럼 목록 조회
    List<CurriculumSummary> findAllByPtCourseId(Long ptCourseId);

    // 커리큘럼 ID로 단건 조회
    CurriculumSummary findById(Long ptCurriculumId);

    // 커리큘럼 ID + 코스 ID로 단건 조회 (피드백 등록 시 해당 코스의 회차인지 검증용)
    CurriculumSummary findByIdAndPtCourseId(Long ptCurriculumId, Long ptCourseId);

    record CurriculumSummary(
            Long ptCurriculumId,
            int sessionNo,
            String title
    ) {}


}
