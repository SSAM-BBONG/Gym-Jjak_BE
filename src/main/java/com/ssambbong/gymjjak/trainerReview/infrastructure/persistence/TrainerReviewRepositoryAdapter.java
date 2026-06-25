package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
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
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConstraintViolationException cve) {
                String name = cve.getConstraintName();
                return name != null && name.contains("uk_trainer_reviews_reservation");
            }
            t = t.getCause();
        }
        return false;
    }
}
