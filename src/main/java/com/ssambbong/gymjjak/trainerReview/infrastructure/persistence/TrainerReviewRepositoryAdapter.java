package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerReviewRepositoryAdapter implements TrainerReviewRepository {

    private final SpringDataTrainerReviewRepository repository;
    private final TrainerReviewPersistenceMapper mapper;

    @Override
    public Long save(TrainerReview trainerReview) {
        try {
            return repository.save(mapper.toEntity(trainerReview)).getId();
        } catch (DataIntegrityViolationException e) {
            if (isReservationUniqueConstraint(e)) {
                throw new TrainerReviewAlreadyExistsException(e);
            }
            throw e;
        }
    }

    @Override
    public boolean existsByPtReservationId(Long ptReservationId) {
        return repository.existsByPtReservationIdAndDeletedAtIsNull(ptReservationId);
    }

    @Override
    public Optional<TrainerReview> findActiveById(Long trainerReviewId) {
        return repository.findByIdAndDeletedAtIsNull(trainerReviewId)
                .map(mapper::toDomain);
    }

    private boolean isReservationUniqueConstraint(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message != null && message.contains("uk_trainer_reviews_reservation");
    }
}
