package com.ssambbong.gymjjak.notification.application.event;

import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationApprovedEvent;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationCanceledEvent;
import com.ssambbong.gymjjak.pt.ptReservation.application.event.PtReservationRequestedEvent;
import com.ssambbong.gymjjak.pt.ptReservation.domain.model.PtReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtReservationNotificationEventListener {

    private final NotificationEventProcessor processor;

    // 예약 확정 알림 수신 - 수강생 수신
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePtReservationApproved(PtReservationApprovedEvent ptReservationApprovedEvent) {

        log.info("event=pt_reservation_approved_notification_received receiverId={}, ptReservationId={}",
                ptReservationApprovedEvent.receiverId(),
                ptReservationApprovedEvent.ptReservationId());
        processor.createSafely(
                ptReservationApprovedEvent.receiverId(),
                NotificationType.PT_RESERVATION_APPROVED,
                ptReservationApprovedEvent.ptReservationId(),
                ptReservationApprovedEvent.occurredAt());
    }

    // PT 예약 신청 알림 — 트레이너 수신
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePtReservationRequested(PtReservationRequestedEvent ptReservationRequestedEvent) {

        log.info("event=pt_reservation_requested_notification_received receiverId={} ptReservationId={}",
                ptReservationRequestedEvent.receiverId(),
                ptReservationRequestedEvent.ptReservationId());
        processor.createSafely(
                ptReservationRequestedEvent.receiverId(),
                NotificationType.PT_RESERVATION_REQUESTED,
                ptReservationRequestedEvent.ptReservationId(),
                ptReservationRequestedEvent.occurredAt()
        );
    }

    // PT 예약 취소 알림 - 수강생 수신
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePtReservationCanceled(PtReservationCanceledEvent ptReservationCanceledEvent) {

        log.info("event=pt_reservation_canceled_notification_received receiverId={} ptReservationId={}",
                ptReservationCanceledEvent.receiverId(),
                ptReservationCanceledEvent.ptReservationId());
        processor.createSafely(
                ptReservationCanceledEvent.receiverId(),
                NotificationType.PT_RESERVATION_CANCELED,
                ptReservationCanceledEvent.ptReservationId(),
                ptReservationCanceledEvent.occurredAt()
        );
    }
}
