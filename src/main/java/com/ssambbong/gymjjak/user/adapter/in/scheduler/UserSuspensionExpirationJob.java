package com.ssambbong.gymjjak.user.adapter.in.scheduler;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJob;
import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;
import com.ssambbong.gymjjak.user.application.port.in.ReleaseExpiredSuspensionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserSuspensionExpirationJob implements RetentionJob {

    private static final String JOB_NAME = "user-suspension-expiration";

    private final ReleaseExpiredSuspensionsUseCase releaseExpiredSuspensionsUseCase;

    @Override
    public String name() {
        return JOB_NAME;
    }

    @Override
    public RetentionJobResult run(LocalDateTime now) {
        return releaseExpiredSuspensionsUseCase.releaseExpiredSuspensions(now);
    }
}
