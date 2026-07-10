package com.ssambbong.gymjjak.community.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommunityRetentionJob implements RetentionJob {

    private final CommunityRetentionService communityRetentionService;

    @Override
    public String name() {
        return CommunityRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return communityRetentionService.hardDeleteExpired(now);
    }
}
