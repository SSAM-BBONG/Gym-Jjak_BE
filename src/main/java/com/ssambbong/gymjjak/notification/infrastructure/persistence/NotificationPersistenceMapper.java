package com.ssambbong.gymjjak.notification.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.config.MapStructConfig;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface NotificationPersistenceMapper {

    NotificationJpaEntity toEntity(Notification notification);

    Notification toDomain(NotificationJpaEntity entity);
}
