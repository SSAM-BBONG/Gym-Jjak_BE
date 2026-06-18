package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerAward;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerAwardJpaEntity;
import org.mapstruct.Mapper;

@Mapper(config =  MapStructConfig.class)
public interface TrainerAwardPersistenceMapper {

    TrainerAwardJpaEntity toEntity(TrainerAward domain);

    TrainerAward toDomain(TrainerAwardJpaEntity  entity);
}
