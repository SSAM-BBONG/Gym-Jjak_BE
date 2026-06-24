package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackNotFoundException;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements FeedbackRepository {

    private final SpringDataFeedbackRepository repository;
    private final FeedbackPersistenceMapper mapper;

    @Override
    public List<Feedback> findAllByPtReservationId(Long ptReservationId) {
        return repository.findAllByPtReservationIdAndDeletedAtIsNull(ptReservationId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Feedback> findById(Long feedbackId) {
        return repository.findByIdAndDeletedAtIsNull(feedbackId)
                .map(mapper::toDomain);
    }

    @Override
    public Feedback save(Feedback feedback) {
        try {
            return mapper.toDomain(repository.save(mapper.toEntity(feedback)));
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateFeedbackViolation(e)) {
                throw new FeedbackAlreadyExistsException();
            }
            throw e;
        }
    }

    @Override
    public void update(Feedback feedback) {
        FeedbackJpaEntity entity = repository.findByIdAndDeletedAtIsNull(feedback.getId())
                .orElseThrow(FeedbackNotFoundException::new);
        entity.update(feedback.getContent());
    }

    private boolean isDuplicateFeedbackViolation(DataIntegrityViolationException e) {
        return Optional.ofNullable(e.getMostSpecificCause())
                .map(Throwable::getMessage)
                .map(msg -> msg.contains("uk_feedbacks_reservation_curriculum"))
                .orElse(false);
    }

    @Override
    public void deleteById(Long feedbackId) {
        repository.findByIdAndDeletedAtIsNull(feedbackId)
                .ifPresent(FeedbackJpaEntity::delete);
    }

    @Override
    public boolean existsByPtReservationIdAndPtCurriculumId(Long ptReservationId, Long ptCurriculumId) {
        return repository.existsByPtReservationIdAndPtCurriculumIdAndDeletedAtIsNull(ptReservationId, ptCurriculumId);
    }
}
