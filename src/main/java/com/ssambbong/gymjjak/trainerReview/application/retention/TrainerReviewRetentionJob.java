package com.ssambbong.gymjjak.trainerReview.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TrainerReviewRetentionJob implements RetentionJob {

    private final TrainerReviewRetentionService trainerReviewRetentionService;

    @Override
    public String name() {
        return TrainerReviewRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return trainerReviewRetentionService.hardDeleteExpiredTrainerReviews(now);
    }
}
