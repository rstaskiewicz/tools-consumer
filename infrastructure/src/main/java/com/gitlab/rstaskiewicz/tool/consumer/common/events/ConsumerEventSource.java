package com.gitlab.rstaskiewicz.tool.consumer.common.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ConsumerEventSource {

    String OUTPUT = "consumer-event-out";

    @Output(OUTPUT)
    MessageChannel output();
}
