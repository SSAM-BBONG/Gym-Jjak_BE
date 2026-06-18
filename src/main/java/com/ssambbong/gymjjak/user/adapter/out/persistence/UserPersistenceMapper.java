package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(config = MapStructConfig.class)
public interface UserPersistenceMapper {

    UserJpaEntity toEntity(User user);

    default User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.reconstruct(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getName(),
                entity.getNickname(),
                entity.getPhone(),
                entity.getRole(),
                entity.getStatus(),
                entity.isOnboardingCompleted(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
