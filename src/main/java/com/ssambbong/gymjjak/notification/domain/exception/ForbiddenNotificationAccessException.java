package com.ssambbong.gymjjak.notification.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ForbiddenException;

public class ForbiddenNotificationAccessException extends ForbiddenException {

    public ForbiddenNotificationAccessException(
            Long requesterId,
            Long notificationId
    ) {
        super(
                NotificationErrorCode.FORBIDDEN_NOTIFICATION_ACCESS,
                "알림에 대한 접근 권한이 없습니다."
        );

        addContext("requesterId", requesterId);
        addContext("notificationId", notificationId);
    }
}
