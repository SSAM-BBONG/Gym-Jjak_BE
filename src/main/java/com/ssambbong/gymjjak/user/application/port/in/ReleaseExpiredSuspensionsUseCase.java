package com.ssambbong.gymjjak.user.application.port.in;

import com.ssambbong.gymjjak.global.application.scheduler.RetentionJobResult;

import java.time.LocalDateTime;

public interface ReleaseExpiredSuspensionsUseCase {

    RetentionJobResult releaseExpiredSuspensions(LocalDateTime now);
}
