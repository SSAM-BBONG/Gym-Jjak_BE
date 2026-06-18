package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.user.domain.model.Blacklist;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(config = MapStructConfig.class)
public interface BlacklistPersistenceMapper {

    BlacklistsJpaEntity toEntity(Blacklist blacklist);

    default Blacklist toDomain(BlacklistsJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Blacklist.of(
                entity.getId(),
                entity.getUserId(),
                entity.getAdminId(),
                entity.getType(),
                entity.getReason(),
                entity.getEndedAt(),
                entity.getStatus(),
                entity.getSourceType(),
                entity.getCreatedAt(),
                entity.getDeletedAt()
        );
    }
}
