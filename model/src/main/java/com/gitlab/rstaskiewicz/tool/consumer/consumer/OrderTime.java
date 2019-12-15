package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
class OrderTime {
    @NonNull Instant when;
}
