package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
public class ConsumerId {
    @NonNull UUID id;
}
