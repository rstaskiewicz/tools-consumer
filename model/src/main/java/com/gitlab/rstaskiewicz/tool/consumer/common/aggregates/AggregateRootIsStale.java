package com.gitlab.rstaskiewicz.tool.consumer.common.aggregates;

public class AggregateRootIsStale extends RuntimeException {

    public AggregateRootIsStale(String msg) {
        super(msg);
    }
}

