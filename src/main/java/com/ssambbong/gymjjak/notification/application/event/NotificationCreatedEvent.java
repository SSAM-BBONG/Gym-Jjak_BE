package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.application.result.NotificationResult;

/* Comment
*   NotificationCommandService -> 알림 DB 저장
*   -> NotificationCreatedEvent 이벤트 발행 -> 트랜잭션 commit
*   -> After commit 리스너 실행 -> websocket 전송
* */

public record NotificationCreatedEvent(
        Long receiverId,
        NotificationResult notification
) {
}
