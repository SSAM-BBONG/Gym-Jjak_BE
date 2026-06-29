package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.organization.organizationApplication.application.event.OrgApplicationApprovedEvent;
import com.ssambbong.gymjjak.organization.organizationApplication.application.event.OrgApplicationRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/* comment.
 *  조직 신청 승인/반려 이벤트를 수신하여 신청자에게 알림을 생성하는 리스너.
 *  OrganizationApplicationCommandService에서 발행한 도메인 이벤트를 받아
 *
 * NotificationEventProcessor를 통해 알림 DB 저장 및 실시간 WebSocket 전송까지 위임한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrgApplicationNotificationEventListener {

    private final NotificationEventProcessor processor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrgApplicationApproved(OrgApplicationApprovedEvent event) {
        log.info(
                "event=org_application_approved_notification_event_received, " +
                        "receiverId={}, organizationApplicationId={}",
                event.receiverId(),
                event.organizationApplicationId()
        );
        processor.createSafely(
                event.receiverId(),
                NotificationType.ORGANIZATION_APPLICATION_APPROVED,
                event.organizationApplicationId(),
                event.occurredAt()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrgApplicationRejected(OrgApplicationRejectedEvent event) {
        log.info(
                "event=org_application_rejected_notification_event_received, " +
                        "receiverId={}, organizationApplicationId={}",
                event.receiverId(),
                event.organizationApplicationId()
        );
        processor.createSafely(
                event.receiverId(),
                NotificationType.ORGANIZATION_APPLICATION_REJECTED,
                event.organizationApplicationId(),
                event.occurredAt()
        );
    }
}
