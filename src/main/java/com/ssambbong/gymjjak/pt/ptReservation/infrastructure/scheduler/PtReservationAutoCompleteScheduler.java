package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.scheduler;

import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtReservationAutoCompleteScheduler {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SpringDataPtReservationRepository ptReservationRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void autoCompleteExpiredReservations() {
        LocalDateTime now = LocalDateTime.now(SEOUL);
        int count = ptReservationRepository.bulkCompleteExpired(now);
        if (count > 0) {
            log.info("event=pt_auto_complete_succeeded count={}", count);
        }
    }
}
