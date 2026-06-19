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
    private final FeedbackMediaPersistenceMapper mapper;

    @Override
    public List<FeedbackMedia> findAllByFeedbackId(Long feedbackId) {
        return repository.findAllByFeedbackIdOrderByIdAsc(feedbackId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<FeedbackMedia> mediaList) {
        List<FeedbackMediaJpaEntity> entities = mediaList.stream()
                .map(mapper::toEntity)
                .toList();
        repository.saveAll(entities);
    }
}
