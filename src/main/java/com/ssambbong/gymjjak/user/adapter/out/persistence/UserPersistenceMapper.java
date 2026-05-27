package com.ssambbong.gymjjak.user.adapter.out.persistence;

import com.ssambbong.gymjjak.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getNickname(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }

    public User toDomain(UserJpaEntity entity) {
        return User.reconstruct(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getName(),
                entity.getNickname(),
                entity.getPhone(),
                entity.getRole(),
                entity.getStatus(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
