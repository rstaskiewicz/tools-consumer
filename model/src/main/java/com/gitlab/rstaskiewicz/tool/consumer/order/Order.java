package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;

public interface Order {

    OrderId getOrderId();

    Version getVersion();
}
