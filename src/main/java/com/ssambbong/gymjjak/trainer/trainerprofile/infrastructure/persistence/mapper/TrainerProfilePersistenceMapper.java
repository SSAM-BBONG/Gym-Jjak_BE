package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfile;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerProfileJpaEntity;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface TrainerProfilePersistenceMapper {

    TrainerProfileJpaEntity toEntity(TrainerProfile domain);

    TrainerProfile toDomain(TrainerProfileJpaEntity entity);
}
