package com.gitlab.rstaskiewicz.tool.consumer.common.events;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JustForwardDomainEventPublisher implements DomainEvents {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
