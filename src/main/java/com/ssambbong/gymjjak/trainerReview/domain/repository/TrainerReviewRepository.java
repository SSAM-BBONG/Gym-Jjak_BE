package com.ssambbong.gymjjak.trainerReview.domain.repository;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListQuery;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListResult;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewSummary;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainerReviewRepository {

    Long save(TrainerReview trainerReview);

    boolean existsByPtReservationId(Long ptReservationId);

    Optional<TrainerReview> findActiveById(Long trainerReviewId);

    TrainerReviewSummary findSummary(Long trainerProfileId);

    TrainerReviewListResult findList(TrainerReviewListQuery query);

    // 소프트딜리트 후 threshold 이전에 삭제된 강사평 ID 배치 조회
    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    // 강사평 물리 삭제
    int hardDeleteByIds(List<Long> ids);
}
