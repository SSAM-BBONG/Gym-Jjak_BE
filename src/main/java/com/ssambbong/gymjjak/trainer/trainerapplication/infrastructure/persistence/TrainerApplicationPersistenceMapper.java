package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface TrainerApplicationPersistenceMapper {

    TrainerApplicationJpaEntity toEntity(TrainerApplication trainerApplication);

    TrainerApplication toDomain(TrainerApplicationJpaEntity entity);
}
