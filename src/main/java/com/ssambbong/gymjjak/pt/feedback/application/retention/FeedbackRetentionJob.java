package com.ssambbong.gymjjak.pt.feedback.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// @Component로 Bean 등록 → GlobalRetentionScheduler의 List<RetentionJob>에 자동 주입
@Component
@RequiredArgsConstructor
public class FeedbackRetentionJob implements RetentionJob {

    private final FeedbackRetentionService feedbackRetentionService;

    @Override
    public String name() {
        return FeedbackRetentionService.JOB_NAME; // Service 상수 참조
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return feedbackRetentionService.hardDeleteExpiredFeedbacks(now);
    }
}
