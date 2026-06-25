package com.ssambbong.gymjjak.category.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CategoryRetentionJob implements RetentionJob {

    private final CategoryRetentionService categoryRetentionService;

    @Override
    public String name() {
        return CategoryRetentionService.JOB_NAME; // Service 상수 참조
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return categoryRetentionService.hardDeleteExpiredCategories(now);
    }
}
