package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvents;
import com.gitlab.rstaskiewicz.tool.consumer.common.events.ConsumerEventSource;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ExternalDomainEventPublisher implements DomainEvents {

    private final ConsumerEventSource source;

    @Override
    public void publish(DomainEvent event) {
        Message<DomainEvent> message = MessageBuilder.withPayload(event)
                .setHeader("type", event.getType())
                .setHeader("aggregateId", event.getAggregateId())
                .build();
        source.output().send(message);
    }
}
