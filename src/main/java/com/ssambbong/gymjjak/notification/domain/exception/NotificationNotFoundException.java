package com.ssambbong.gymjjak.notification.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class NotificationNotFoundException extends NotFoundException {

    public NotificationNotFoundException(Long notificationId) {
        super(
                NotificationErrorCode.NOTIFICATION_NOT_FOUND,
                "알림을 찾을 수 없습니다."
        );
        addContext("notificationId", notificationId);
    }
}
