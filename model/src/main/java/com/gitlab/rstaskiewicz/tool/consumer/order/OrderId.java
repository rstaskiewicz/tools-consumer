package com.gitlab.rstaskiewicz.tool.consumer.order;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
public class OrderId {
    @NonNull UUID id;
}
