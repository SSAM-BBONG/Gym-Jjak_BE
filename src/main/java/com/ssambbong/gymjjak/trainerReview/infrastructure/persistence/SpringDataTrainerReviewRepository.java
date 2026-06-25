package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataTrainerReviewRepository extends JpaRepository<TrainerReviewJpaEntity, Long> {

    boolean existsByPtReservationIdAndDeletedAtIsNull(Long ptReservationId);

    Optional<TrainerReviewJpaEntity> findByIdAndDeletedAtIsNull(Long id);
}
