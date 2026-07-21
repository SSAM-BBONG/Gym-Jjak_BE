package com.ssambbong.gymjjak.notification.application.usecase;

import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;
import com.ssambbong.gymjjak.notification.application.result.UnreadNotificationCountResult;

public interface NotificationQueryUseCase {

    NotificationListResult findNotifications(FindNotificationsQuery query);

    UnreadNotificationCountResult findUnreadNotificationCount(Long receiverId);
}
