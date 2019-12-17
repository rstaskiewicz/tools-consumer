package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

class EventDescriptor {

    @Id
    private Long id;

    @NonNull UUID eventId;
    @NonNull UUID aggregateId;
    @NonNull String body;
    @NonNull String type;
    @NonNull Instant occurredAt;

    public EventDescriptor(@NonNull UUID eventId,
                           @NonNull UUID aggregateId,
                           @NonNull String body,
                           @NonNull String type,
                           @NonNull Instant occurredAt) {
        this.body = body;
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.type = type;
        this.occurredAt = occurredAt;
    }
}
