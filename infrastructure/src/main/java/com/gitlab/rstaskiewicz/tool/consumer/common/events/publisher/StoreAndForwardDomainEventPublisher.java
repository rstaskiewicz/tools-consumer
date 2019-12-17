package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Primary
@Component
@RequiredArgsConstructor
class StoreAndForwardDomainEventPublisher implements DomainEvents {

    @Qualifier("externalDomainEventPublisher")
    private final DomainEvents externalDomainEvents;

    @Qualifier("internalDomainEventPublisher")
    private final DomainEvents internalDomainEvents;

    private final EventsStorage eventStorage;
    private final EventSerializer eventSerializer;

    @Override
    public void publish(DomainEvent event) {
        eventStorage.save(eventSerializer.serialize(event));
    }

    @Transactional
    @Scheduled(fixedRate = 3000)
    public void publishPeriodically() {
        List<EventDescriptor> eventDescriptors = eventStorage.fetch();
        List<DomainEvent> events = eventDescriptors.stream()
                .map(eventSerializer::deserialize)
                .collect(toList());
        internalDomainEvents.publish(events);
        externalDomainEvents.publish(events);
        eventStorage.published(eventDescriptors);
    }
}
