package com.ssambbong.gymjjak.notification.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {

    @Query("""
        select n
        from NotificationJpaEntity n
        where n.receiverId = :receiverId
          and n.deletedAt is null
          and n.expiresAt > :now
        order by n.createdAt desc, n.notificationId desc
        """)
    Slice<NotificationJpaEntity> findNotifications(
            @Param("receiverId") Long receiverId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query(
            value = "select notification_id " +
                    "FROM notifications " +
                    "where deleted_at is not null " +
                    "and deleted_at < :threshold " +
                    "order by deleted_at asc, notification_id asc " +
                    "limit :batchSize",
            nativeQuery = true
    )
    List<Long> findHardDeleteCandidateIds(
            @Param("threshold") LocalDateTime threshold,
            @Param("batchSize") int batchSize
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = "delete from notifications " +
                    "where notification_id in :ids " +
                    "and deleted_at is not null " +
                    "and deleted_at < :threshold",
            nativeQuery = true
    )
    int hardDeleteByIds(
            @Param("ids") List<Long> Ids,
            @Param("threshold") LocalDateTime threshold);
}
