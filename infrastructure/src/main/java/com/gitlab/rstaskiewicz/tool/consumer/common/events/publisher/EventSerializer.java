package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
@Component
class EventSerializer {

    private final ObjectMapper objectMapper;

    public EventSerializer() {
        this.objectMapper = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    EventDescriptor serialize(DomainEvent event) {
        try {
            return new EventDescriptor(
                    event.getEventId(),
                    event.getAggregateId(),
                    objectMapper.writeValueAsString(event),
                    event.getType(),
                    event.getWhen());
        } catch (JsonProcessingException e) {
            log.warn("Event with id: {} for aggregate: {} was not serialized", event.getEventId(), event.getAggregateId());
            throw new AggregateEventSerializationException("Event cannot be serialized: " + event.getEventId());
        }
    }

    DomainEvent deserialize(EventDescriptor descriptor) {
        try {
            return objectMapper.readValue(descriptor.body, DomainEvent.class);
        } catch (JsonProcessingException e) {
            log.warn("Event with id: {} for aggregate: {} was not deserialized", descriptor.eventId, descriptor.occurredAt);
            throw new AggregateEventSerializationException("Event cannot be deserialized: " + descriptor.eventId);
        }
    }
}
