package com.ssambbong.gymjjak.pt.ptCourse.domain.repository;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourse;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;

import java.util.List;
import java.util.Optional;

public interface PtCourseRepository {

    // PT 등록
    PtCourse save(PtCourse ptCourse);

    // 단건 조회
    Optional<PtCourse> findById(Long id);

    // VISIBLE 상태 전체 목록 조회
    List<PtCourse> findAllVisible();

    // 상태 변경 (blind/unblind/delete)
    void update(PtCourse ptCourse);

    // status=null → VISIBLE+HIDDEN 전체 / status 지정 → 해당 status만
    List<PtCourse> findAllByTrainerProfileId(Long trainerProfileId, PtCourseStatus status);
}
