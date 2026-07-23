package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCurriculum;

import java.util.List;
import java.util.Optional;

public interface PtCurriculumRepository {
    List<PtCurriculum> saveAll(List<PtCurriculum> curriculums);
    List<PtCurriculum> findAllByPtCourseId(Long ptCourseId);
    Optional<PtCurriculum> findById(Long ptCurriculumId);
    // 커리큘럼 ID + 코스 ID로 단건 조회
    Optional<PtCurriculum> findByIdAndPtCourseId(Long ptCurriculumId, Long ptCourseId);
    void update(PtCurriculum curriculum);
    void deleteAllByIdIn(List<Long> ids);
    // 대기 중인 삭제·수정을 즉시 DB에 반영한다 — Hibernate는 flush 시 삽입을 수정보다 먼저
    // 실행하므로, session_no를 다른 값으로 옮기는 수정과 신규 삽입이 겹칠 때 유니크 제약
    // 충돌을 막으려면 수정을 먼저 flush해야 한다.
    void flush();
    // PT 강습 ID 목록에 속한 커리큘럼 하드딜리트
    int hardDeleteByPtCourseIds(List<Long> ptCourseIds);
}
