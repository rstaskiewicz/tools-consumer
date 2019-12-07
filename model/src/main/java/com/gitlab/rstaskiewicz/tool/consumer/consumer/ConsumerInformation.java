package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.NonNull;
import lombok.Value;

@Value
class ConsumerInformation {
    @NonNull ConsumerId consumerId;
}
