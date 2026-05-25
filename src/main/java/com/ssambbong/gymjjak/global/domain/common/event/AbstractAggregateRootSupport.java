package com.ssambbong.gymjjak.global.domain.common.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Comment
*   해당 클래스는 기존, domain 속 aggregate 마다 있던
*   pullDomainEvents() 메서드를 공통 서식으로 만든 클래스
*   입니다!
* */
public abstract class AbstractAggregateRootSupport {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
