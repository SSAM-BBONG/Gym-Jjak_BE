package com.ssambbong.gymjjak.global.application.scheduler;

import java.time.LocalDateTime;

// 각 도메인이 구현할 스케줄 작업 계약
public interface RetentionJob {

    String name();

    RetentionResult run(LocalDateTime now);
}
