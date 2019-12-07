package com.gitlab.rstaskiewicz.tool.consumer.common.aggregates;

public class AggregateRootNotFoundException extends RuntimeException {

    public AggregateRootNotFoundException(String message) {
        super(message);
    }
}
