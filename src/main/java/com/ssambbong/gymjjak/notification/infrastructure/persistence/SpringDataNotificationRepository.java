package com.ssambbong.gymjjak.notification.infrastructure.persistence;

import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, Long> {

    @Query("""
        select n
        from NotificationJpaEntity n
        where n.receiverId = :receiverId
          and n.deletedAt is null
          and n.expiresAt > :now
        order by n.createdAt desc
        """)
    Slice<NotificationJpaEntity> findNotifications(
            @Param("receiverId") Long receiverId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
