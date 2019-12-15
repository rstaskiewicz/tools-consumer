package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import io.vavr.control.Option;

@FunctionalInterface
public interface FindPlacedOrder {

    Option<PlacedOrder> findPlacedOrder(OrderId orderId);
}
