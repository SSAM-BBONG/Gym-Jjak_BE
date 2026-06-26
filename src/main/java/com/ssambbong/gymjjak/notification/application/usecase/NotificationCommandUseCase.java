package com.ssambbong.gymjjak.notification.application.usecase;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;

// 알림 생성 이벤트 생성 usecase
public interface NotificationCommandUseCase {

    NotificationResult createNotification(CreateNotificationCommand command);
}
