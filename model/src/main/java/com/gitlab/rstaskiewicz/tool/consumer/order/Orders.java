package com.gitlab.rstaskiewicz.tool.consumer.order;

import io.vavr.control.Option;

public interface Orders {

    Option<Order> findBy(OrderId orderId);

    void save(Order order);
}
