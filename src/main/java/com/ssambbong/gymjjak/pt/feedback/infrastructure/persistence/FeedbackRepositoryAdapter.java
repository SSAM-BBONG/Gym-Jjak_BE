package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements FeedbackRepository {

    private final SpringDataFeedbackRepository repository;

    @Override
    public List<Feedback> findAllByPtReservationId(Long ptReservationId) {
        return repository.findAllByPtReservationIdAndDeletedAtIsNull(ptReservationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Feedback> findById(Long feedbackId) {
        return repository.findByIdAndDeletedAtIsNull(feedbackId)
                .map(this::toDomain);
    }

    private Feedback toDomain(FeedbackJpaEntity entity) {
        return Feedback.restore(
                entity.getId(),
                entity.getPtReservationId(),
                entity.getPtCurriculumId(),
                entity.getTrainerProfileId(),
                entity.getUserId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
