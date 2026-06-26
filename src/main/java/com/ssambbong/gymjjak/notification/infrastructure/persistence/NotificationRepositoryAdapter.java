package com.ssambbong.gymjjak.notification.infrastructure.persistence;

import com.ssambbong.gymjjak.notification.application.port.out.NotificationQueryPort;
import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import com.ssambbong.gymjjak.notification.domain.model.Notification;
import com.ssambbong.gymjjak.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository, NotificationQueryPort {

    private final SpringDataNotificationRepository repository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity =
                mapper.toEntity(notification);

        NotificationJpaEntity savedEntity =
                repository.save(entity);

        return mapper.toDomain(savedEntity);


    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        List<NotificationJpaEntity> entities =
                notifications.stream()
                        .map(mapper::toEntity)
                        .toList();

        return repository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return repository.findById(notificationId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Notification> findAllById(List<Long> notificationIds) {
        return repository.findAllById(notificationIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Long> findHardDeleteCandidateIds(LocalDateTime threshold, int batchSize) {
        return repository.findHardDeleteCandidateIds(threshold, batchSize);
    }

    @Override
    public int hardDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        return repository.hardDeleteByIds(ids);
    }

    @Override
    public NotificationListResult findNotifications(FindNotificationsQuery query) {
        PageRequest pageRequest =
                PageRequest.of(
                        query.page(),
                        query.size()
                );

        Slice<NotificationJpaEntity> slice =
                repository.findNotifications(
                        query.receiverId(),
                        LocalDateTime.now(),
                        pageRequest
                );

        return new NotificationListResult(
                slice.getContent().stream()
                        .map(mapper::toDomain)
                        .map(NotificationResult::from)
                        .toList(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}
