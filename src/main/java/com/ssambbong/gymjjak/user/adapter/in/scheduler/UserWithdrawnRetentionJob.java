package com.ssambbong.gymjjak.user.adapter.in.scheduler;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.user.application.port.in.DeleteWithdrawnUsersUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.retention.withdrawn-user",
        name = "enabled",
        havingValue = "true"
)
public class UserWithdrawnRetentionJob implements RetentionJob {

    private static final String JOB_NAME = "user-withdrawn-retention";

    private final DeleteWithdrawnUsersUsecase deleteWithdrawnUsersUseCase;

    @Override
    public String name() {
        return JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return deleteWithdrawnUsersUseCase.deleteExpiredWithdrawnUsers(now);
    }
}
