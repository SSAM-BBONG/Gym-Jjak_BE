package com.ssambbong.gymjjak.notification.application.usecase;

import com.ssambbong.gymjjak.notification.application.command.CreateNotificationCommand;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;

public interface NotificationCommandUseCase {

    NotificationResult createNotification(CreateNotificationCommand command);
}
