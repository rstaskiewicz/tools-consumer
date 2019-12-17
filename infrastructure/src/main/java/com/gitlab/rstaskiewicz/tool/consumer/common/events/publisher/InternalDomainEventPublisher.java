package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class InternalDomainEventPublisher implements DomainEvents {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
