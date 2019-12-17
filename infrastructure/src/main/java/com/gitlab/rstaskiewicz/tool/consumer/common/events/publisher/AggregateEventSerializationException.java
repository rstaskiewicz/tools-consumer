package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

class AggregateEventSerializationException extends RuntimeException {

    AggregateEventSerializationException(String message) {
        super(message);
    }
}
