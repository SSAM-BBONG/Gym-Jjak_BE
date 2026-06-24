package com.ssambbong.gymjjak.trainerReview.domain.repository;

import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;

import java.util.Optional;

public interface TrainerReviewRepository {

    Long save(TrainerReview trainerReview);

    boolean existsByPtReservationId(Long ptReservationId);

    Optional<TrainerReview> findActiveById(Long trainerReviewId);
}
