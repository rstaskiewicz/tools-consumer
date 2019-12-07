package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import io.vavr.control.Option;

@FunctionalInterface
interface FindPlacedOrder {

    Option<PlacedOrder> findBy(OrderId orderId, ConsumerId consumerId);
}
