package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.mapper;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerCertification;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity.TrainerCertificationJpaEntity;
import org.mapstruct.Mapper;

@Mapper(config =  MapStructConfig.class)
public interface TrainerCertificationPersistenceMapper {

    TrainerCertificationJpaEntity toEntity(TrainerCertification domain);

    TrainerCertification toDomain(TrainerCertificationJpaEntity entity);
}
