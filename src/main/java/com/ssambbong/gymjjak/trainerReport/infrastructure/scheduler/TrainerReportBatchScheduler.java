package com.ssambbong.gymjjak.trainerReport.infrastructure.scheduler;

import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository;
import com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.repository.SpringDataTrainerProfileRepository.TrainerProfileIdAndUserId;
import com.ssambbong.gymjjak.trainerReport.application.service.TrainerReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 매달 1일 새벽, 직전 달 데이터를 기준으로 활성 트레이너 전원의 리포트를 생성한다.
 * 말일에 돌리지 않는 이유: 그날 발생한 데이터가 아직 다 안 쌓인 상태일 수 있어서(데이터 완결성).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerReportBatchScheduler {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SpringDataTrainerProfileRepository trainerProfileRepository;
    private final TrainerReportService trainerReportService;

    @Scheduled(cron = "0 30 0 1 * *", zone = "Asia/Seoul")
    public void generateMonthlyReports() {
        LocalDate targetMonth = LocalDate.now(SEOUL).minusMonths(1).withDayOfMonth(1);

        List<TrainerProfileIdAndUserId> trainers =
                trainerProfileRepository.findAllIdAndUserIdByStatus(TrainerProfileStatus.ACTIVE);

        log.info("event=trainer_report_batch_started targetMonth={} trainerCount={}",
                targetMonth, trainers.size());

        int successCount = 0;
        int failureCount = 0;
        for (TrainerProfileIdAndUserId trainer : trainers) {
            try {
                trainerReportService.generateReport(trainer.getTrainerProfileId(), trainer.getUserId(), targetMonth);
                successCount++;
            } catch (RuntimeException exception) {
                failureCount++;
                // 트레이너 한 명의 실패가 나머지 트레이너 리포트 생성을 막지 않도록 격리한다.
                log.error("event=trainer_report_generation_failed trainerProfileId={} targetMonth={}",
                        trainer.getTrainerProfileId(), targetMonth, exception);
            }
        }

        log.info("event=trainer_report_batch_completed targetMonth={} successCount={} failureCount={}",
                targetMonth, successCount, failureCount);
    }
}
