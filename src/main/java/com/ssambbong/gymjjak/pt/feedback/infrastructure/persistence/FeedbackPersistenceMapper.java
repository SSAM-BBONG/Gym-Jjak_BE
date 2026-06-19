package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface FeedbackPersistenceMapper {

    FeedbackJpaEntity toEntity(Feedback feedback);

    Feedback toDomain(FeedbackJpaEntity entity);
}
