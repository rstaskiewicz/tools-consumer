package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@Value
class ConsumerOrders {

    static int MAX_NUMBER_OF_DAILY_HOLDS = 3;

    Map<LocalDate, Set<ConsumerOrder>> placedOrders;

    boolean contains(@NonNull PlacedOrder placedOrder) {
        var order = new ConsumerOrder(placedOrder.getOrderId(), placedOrder.getPlacedAt());
        return placedOrders.values().stream()
                .flatMap(Collection::stream)
                .collect(toSet())
                .contains(order);
    }

    int countToday() {
        return placedOrders.getOrDefault(LocalDate.now(), emptySet()).size();
    }

    boolean reachedMaximumDailyOrdersAfterPlacing(OrderId order) {
        return countToday() + 1 == MAX_NUMBER_OF_DAILY_HOLDS;
    }
}
