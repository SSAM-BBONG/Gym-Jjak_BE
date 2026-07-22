package com.ssambbong.gymjjak.organization.organizationApplication.application.event;

import com.ssambbong.gymjjak.global.domain.common.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record OrgApplicationRejectedEvent(
        Long receiverId,
        Long organizationApplicationId,
        Instant occurredAt
) implements DomainEvent {

    public OrgApplicationRejectedEvent {
        Objects.requireNonNull(receiverId, "receiverId는 필수입니다.");
        Objects.requireNonNull(organizationApplicationId, "organizationApplicationId는 필수입니다.");
        Objects.requireNonNull(occurredAt, "occurredAt은 필수입니다.");
    }

    public OrgApplicationRejectedEvent(Long receiverId, Long organizationApplicationId) {
        this(receiverId, organizationApplicationId, Instant.now());
    }
}
