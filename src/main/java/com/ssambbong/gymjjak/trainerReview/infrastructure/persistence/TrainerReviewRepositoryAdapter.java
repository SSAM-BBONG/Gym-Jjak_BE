package com.ssambbong.gymjjak.trainerReview.infrastructure.persistence;

import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewItem;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListQuery;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewListResult;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewSortType;
import com.ssambbong.gymjjak.trainerReview.application.query.TrainerReviewSummary;
import com.ssambbong.gymjjak.trainerReview.domain.exception.TrainerReviewAlreadyExistsException;
import com.ssambbong.gymjjak.trainerReview.domain.model.TrainerReview;
import com.ssambbong.gymjjak.trainerReview.domain.repository.TrainerReviewRepository;
import com.ssambbong.gymjjak.pt.ptCourse.application.port.ReviewQueryPort;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerReviewRepositoryAdapter implements TrainerReviewRepository, ReviewQueryPort {

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

    @Override
    public TrainerReviewSummary findSummary(Long trainerProfileId) {
        TrainerReviewSummaryProjection summary = repository.findSummary(trainerProfileId);
        if (summary == null) {
            throw new com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception.TrainerProfileNotFoundException("trainerProfileId", trainerProfileId);
        }
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(5, summary.getRating5Count());
        distribution.put(4, summary.getRating4Count());
        distribution.put(3, summary.getRating3Count());
        distribution.put(2, summary.getRating2Count());
        distribution.put(1, summary.getRating1Count());
        return new TrainerReviewSummary(
                summary.getTrainerName(), summary.getIntroduction(),
                summary.getAverageRating(), summary.getReviewCount(), distribution);
    }

    @Override
    public TrainerReviewListResult findList(TrainerReviewListQuery query) {
        List<TrainerReviewProjection> rows = query.sort() == TrainerReviewSortType.HIGH_RATING
                ? repository.findHighRatingReviews(query.trainerProfileId(), query.cursor(), query.cursorRating(), query.size() + 1)
                : repository.findLatestReviews(query.trainerProfileId(), query.cursor(), query.size() + 1);

        boolean hasNext = rows.size() > query.size();
        List<TrainerReviewItem> reviews = rows.stream()
                .limit(query.size())
                .map(p -> new TrainerReviewItem(
                        p.getTrainerReviewId(),
                        p.getNickname(),
                        p.getPtCourseTitle(),
                        p.getRating(),
                        p.getContent(),
                        p.getCreatedAt()
                ))
                .toList();

        Long nextCursor = hasNext ? reviews.get(reviews.size() - 1).trainerReviewId() : null;
        Integer nextCursorRating = (hasNext && query.sort() == TrainerReviewSortType.HIGH_RATING)
                ? reviews.get(reviews.size() - 1).rating() : null;

        return new TrainerReviewListResult(reviews, nextCursor, nextCursorRating, hasNext);
    }

    @Override
    public long countActive() {
        return repository.countActive();
    }

    @Override
    public double findAverageRating() {
        return repository.findAverageRating();
    }

    @Override
    public List<ReviewQueryPort.ReviewSummary> findRecentByTrainerProfileId(Long trainerProfileId, int limit) {
        return repository.findRecentByTrainerProfileId(trainerProfileId, limit).stream()
                .map(p -> new ReviewQueryPort.ReviewSummary(
                        p.getTrainerReviewId(),
                        p.getRating(),
                        p.getContent(),
                        p.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize) {
        return repository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    @Transactional
    public int hardDeleteByIds(List<Long> ids) {
        if (ids.isEmpty()) return 0; // 빈 IN절 쿼리 방지
        return repository.hardDeleteByIds(ids);
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
