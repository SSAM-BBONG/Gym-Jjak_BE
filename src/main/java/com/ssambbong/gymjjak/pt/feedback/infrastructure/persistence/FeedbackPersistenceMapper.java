package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.pt.feedback.domain.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface FeedbackPersistenceMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    FeedbackJpaEntity toEntity(Feedback feedback);

    Feedback toDomain(FeedbackJpaEntity entity);
}
