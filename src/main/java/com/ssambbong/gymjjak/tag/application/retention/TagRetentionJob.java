package com.ssambbong.gymjjak.tag.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// @Component로 Bean 등록 → GlobalRetentionScheduler의 List<RetentionJob>에 자동 주입
@Component
@RequiredArgsConstructor
public class TagRetentionJob implements RetentionJob {

    private final TagRetentionService tagRetentionService;

    @Override
    public String name() {
        return TagRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return tagRetentionService.hardDeleteExpiredTags(now);
    }
}
