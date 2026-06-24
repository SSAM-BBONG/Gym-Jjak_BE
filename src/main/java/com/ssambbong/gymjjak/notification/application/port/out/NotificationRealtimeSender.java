package com.ssambbong.gymjjak.notification.application.port.out;

import com.ssambbong.gymjjak.notification.application.result.NotificationResult;

/**
 * 알림을 사용자에게 실시간으로 특정 사용자에게 보내는 클래스
 */
public interface NotificationRealtimeSender {

    void sendToUser(
            Long receiverId,
            NotificationResult notification
    );
}
