package com.ssambbong.gymjjak.notification.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class InvalidNotificationException extends BadRequestException {

    public InvalidNotificationException(String reason) {
        super(
                NotificationErrorCode.INVALID_NOTIFICATION_REQUEST,
                reason
        );
    }
}
