package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import io.vavr.control.Option;

public interface Consumers {

    Option<Consumer> findBy(ConsumerId consumerId);

    Consumer publish(ConsumerEvent event);
}
