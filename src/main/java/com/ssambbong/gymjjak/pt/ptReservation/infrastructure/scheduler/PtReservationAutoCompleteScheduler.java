package com.ssambbong.gymjjak.pt.ptReservation.infrastructure.scheduler;

import com.ssambbong.gymjjak.pt.ptReservation.infrastructure.persistence.SpringDataPtReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtReservationAutoCompleteScheduler {

    private final SpringDataPtReservationRepository ptReservationRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void autoCompleteExpiredReservations() {
        int inProgress = ptReservationRepository.bulkUpdateToInProgress();
        if (inProgress > 0) {
            log.info("event=pt_auto_in_progress_succeeded count={}", inProgress);
        }
        int completed = ptReservationRepository.bulkCompleteAll();
        if (completed > 0) {
            log.info("event=pt_auto_complete_succeeded count={}", completed);
        }
    }
}
