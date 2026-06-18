package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;
import com.ssambbong.gymjjak.pt.feedback.domain.repository.FeedbackMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedbackMediaRepositoryAdapter implements FeedbackMediaRepository {

    private final SpringDataFeedbackMediaRepository repository;

    @Override
    public List<FeedbackMedia> findAllByFeedbackId(Long feedbackId) {
        return repository.findAllByFeedbackIdOrderByIdAsc(feedbackId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private FeedbackMedia toDomain(FeedbackMediaJpaEntity entity) {
        return FeedbackMedia.restore(
                entity.getId(),
                entity.getFeedbackId(),
                entity.getMediaType(),
                entity.getFileId()
        );
    }
}
