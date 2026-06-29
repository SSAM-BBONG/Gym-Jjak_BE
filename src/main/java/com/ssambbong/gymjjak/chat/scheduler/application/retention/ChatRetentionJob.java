package com.ssambbong.gymjjak.chat.scheduler.application.retention;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatRetentionJob implements RetentionJob {

    private final ChatRetentionService chatRetentionService;

    @Override
    public String name() {
        return ChatRetentionService.JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return chatRetentionService.hardDeleteExpired(now);
    }
}
