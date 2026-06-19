package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.feedback.domain.exception.FeedbackAlreadyExistsException;
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
            throw new FeedbackAlreadyExistsException();
        }
    }

    @Override
    public boolean existsByPtReservationIdAndPtCurriculumId(Long ptReservationId, Long ptCurriculumId) {
        return repository.existsByPtReservationIdAndPtCurriculumIdAndDeletedAtIsNull(ptReservationId, ptCurriculumId);
    }
}
