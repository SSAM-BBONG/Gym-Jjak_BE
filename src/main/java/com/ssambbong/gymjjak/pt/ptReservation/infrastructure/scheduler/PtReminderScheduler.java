package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.scheduler;

import com.ssambbong.gymjjak.notification.application.event.NotificationEventProcessor;
import com.ssambbong.gymjjak.notification.domain.type.NotificationType;
import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtReminderScheduler {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SpringDataPtReservationRepository ptReservationRepository;
    private final NotificationEventProcessor notificationEventProcessor;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void sendPtReminders() {
        LocalDateTime now = LocalDateTime.now(SEOUL);
        LocalDateTime from = now.plusHours(1);
        LocalDateTime to = now.plusHours(1).plusMinutes(1);

        log.info("event=pt_reminder_scheduler_started from={}, to={}", from, to);

        ptReservationRepository.findReservationsStartingBetween(from, to)
                .forEach(row -> notificationEventProcessor.createSafely(
                        ((Number) row[0]).longValue(),
                        NotificationType.PT_REMINDER,
                        ((Number) row[1]).longValue(),
                        Instant.now()
                ));

        log.info("event=pt_reminder_scheduler_completed");
    }
}
