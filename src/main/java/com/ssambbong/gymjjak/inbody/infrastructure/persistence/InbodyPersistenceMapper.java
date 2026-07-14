package com.ssambbong.gymjjak.inbody.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.inbody.domain.model.Inbody;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface InbodyPersistenceMapper {

    InbodyJpaEntity toEntity(Inbody domain);

    Inbody toDomain(InbodyJpaEntity entity);
}
