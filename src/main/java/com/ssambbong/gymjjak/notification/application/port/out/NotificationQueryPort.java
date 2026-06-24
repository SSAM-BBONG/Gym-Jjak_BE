package com.ssambbong.gymjjak.notification.application.port.out;

import com.ssambbong.gymjjak.notification.application.query.FindNotificationsQuery;
import com.ssambbong.gymjjak.notification.application.result.NotificationListResult;

public interface NotificationQueryPort {

    NotificationListResult findNotifications(FindNotificationsQuery query);
}
