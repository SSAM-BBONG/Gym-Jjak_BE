package com.ssambbong.gymjjak.notification.application.port.out;

import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;

import java.time.LocalDateTime;

public interface NotificationQueryPort {

    NotificationListResult findNotifications(FindNotificationsQuery query);

    // 안읽은 알림 조회
    long countUnreadNotifications(Long receiverId, LocalDateTime now);
}
