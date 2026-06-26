package com.ssambbong.gymjjak.trainerReview.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// @Component로 Bean 등록 → GlobalRetentionScheduler의 List<RetentionJob>에 자동 주입
@Component
@RequiredArgsConstructor
public class TrainerReviewRetentionJob implements RetentionJob {

    private final TrainerReviewRetentionService trainerReviewRetentionService;

    @Override
    public String name() {
        return TrainerReviewRetentionService.JOB_NAME; // Service 상수 참조
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return trainerReviewRetentionService.hardDeleteExpiredTrainerReviews(now);
    }
}
