package com.ssambbong.gymjjak.notification.application.usecase;

import com.ssambbong.gymjjak.notification.application.command.MarkNotificationReadCommand;
import com.ssambbong.gymjjak.notification.application.result.MarkNotificationReadResult;

// 사용자 알림 api용
public interface NotificationUserCommandUseCase {

    MarkNotificationReadResult readNotifications(MarkNotificationReadCommand command);
}
