package com.ssambbong.gymjjak.pt.feedback.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.pt.feedback.domain.model.FeedbackMedia;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface FeedbackMediaPersistenceMapper {

    FeedbackMediaJpaEntity toEntity(FeedbackMedia feedbackMedia);

    FeedbackMedia toDomain(FeedbackMediaJpaEntity entity);
}
