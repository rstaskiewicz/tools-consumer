package com.gitlab.rstaskiewicz.tool.consumer.common.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    Instant getWhen();
}
