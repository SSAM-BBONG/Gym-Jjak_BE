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

    long countActive();

    double findAverageRating();

    List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize);

    int hardDeleteByIds(List<Long> ids);
}
