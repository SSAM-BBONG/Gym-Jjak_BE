package com.ssambbong.gymjjak.global.domain.common.event;

import java.time.Instant;

public interface DomainEvent {

    /* 프로젝트 내 Event 발생시 공통적으로 포함되는 결과값 */

    Instant occurredAt();
}
