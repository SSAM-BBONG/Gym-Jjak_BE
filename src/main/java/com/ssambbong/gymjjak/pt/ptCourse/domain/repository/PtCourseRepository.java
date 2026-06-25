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

    // 커리큘럼 수정 시 동시 예약 삽입 방지용 비관적 잠금 조회
    Optional<PtCourse> findByIdForUpdate(Long id);

    // VISIBLE 상태 전체 목록 조회
    List<PtCourse> findAllVisible();

    // 상태 변경 (blind/unblind/delete)
    void update(PtCourse ptCourse);

    // status=null → VISIBLE+HIDDEN 전체 / status 지정 → 해당 status만
    List<PtCourse> findAllByTrainerProfileId(Long trainerProfileId, PtCourseStatus status);

    // 예약 수 기준 인기 강습 조회 (VISIBLE, soft delete 제외)
    List<PtCourse> findPopular(int limit);

    // 소프트딜리트 > threshold PT ID 배치 조회
    List<Long> findHardDeleteCandidateIds(java.time.LocalDateTime threshold, int batchSize);

    // 하드딜리트
    int hardDeleteByIds(List<Long> ids);
}
