package com.ssambbong.gymjjak.notification.infrastructure.websocket;

import com.ssambbong.gymjjak.notification.application.port.out.NotificationRealtimeSender;
import com.ssambbong.gymjjak.notification.application.result.NotificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 알림 도메인에서 생성된 알림을 websocket으로 실시간 전송하는 Adapter 클래스 (Port 역할)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketNotificationSender implements NotificationRealtimeSender {

    private static final String NOTIFICATION_DESTINATION =
            "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToUser(Long receiverId, NotificationResult notification) {
        if (receiverId == null || notification == null) {
            log.warn(
                    "event=notification_realtime_send_skipped, reason=invalid_argument, receiverId={}",
                    receiverId
            );
            return;
        }

        messagingTemplate.convertAndSendToUser(
                receiverId.toString(), // 알림 받을 사용자
                NOTIFICATION_DESTINATION, // 사용자 개인 queue 경로
                notification // 실제로 보낼 데이터
        );

        log.info(
                "event=notification_realtime_sent, receiverId={}, notificationId={}, destination={}",
                receiverId,
                notification.notificationId(),
                NOTIFICATION_DESTINATION
        );
    }
}
