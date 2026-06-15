package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.Blacklist;
import org.springframework.stereotype.Component;

@Component
public class BlacklistPersistenceMapper {

    public BlacklistsJpaEntity toEntity(Blacklist blacklist) {
        return BlacklistsJpaEntity.of(
                blacklist.getId(),
                blacklist.getUserId(),
                blacklist.getAdminId(),
                blacklist.getType(),
                blacklist.getReason(),
                blacklist.getEndedAt(),
                blacklist.getStatus(),
                blacklist.getSourceType(),
                blacklist.getCreatedAt(),
                blacklist.getDeletedAt()
        );
    }

    public Blacklist toDomain(BlacklistsJpaEntity entity) {
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
