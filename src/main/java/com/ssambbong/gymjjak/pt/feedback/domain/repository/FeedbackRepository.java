package com.ssambbong.gymjjak.pt.feedback.domain.repository;

import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {

    // 예약 ID로 피드백 목록 조회
    List<Feedback> findAllByPtReservationId(Long ptReservationId);

    // 예약 ID 목록으로 피드백 전체 조회 (코스 전체 세션 피드백 조회용)
    List<Feedback> findAllByPtReservationIds(List<Long> ptReservationIds);

    // 피드백 ID로 단건 조회
    Optional<Feedback> findById(Long feedbackId);

    // 피드백 등록
    Feedback save(Feedback feedback);

    // 피드백 내용 수정
    void update(Feedback feedback);

    // 코스 전체 세션 중 동일 커리큘럼 피드백 중복 여부 확인
    boolean existsByPtReservationIdsAndPtCurriculumId(List<Long> ptReservationIds, Long ptCurriculumId);

    // 피드백 삭제
    void deleteById(Long feedbackId);

    // 소프트딜리트된 지 threshold 초과한 피드백 ID 배치 조회
    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    // 하드딜리트
    int hardDeleteByIds(List<Long> ids);

    // PT 강습 ID 목록에 속한 피드백 ID 조회 (리텐션 삭제 순서 보장용)
    List<Long> findIdsByPtCourseIds(List<Long> ptCourseIds);
}
