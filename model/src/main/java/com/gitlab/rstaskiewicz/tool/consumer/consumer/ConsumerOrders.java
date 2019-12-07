package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

import static java.util.function.Function.identity;

@Value
class ConsumerOrders {

    static int MAX_NUMBER_OF_DAILY_HOLDS = 3;

    Map<LocalDate, Set<ConsumerOrder>> placedOrders;

    boolean contains(@NonNull PlacedOrder placedOrder) {
        var order = new ConsumerOrder(placedOrder.getOrderId(), placedOrder.getSalesBranchId());
        return placedOrders.values()
                .flatMap(identity())
                .contains(order);
    }

    int countToday() {
        return placedOrders.getOrElse(LocalDate.now(), HashSet.empty()).size();
    }

    boolean reachedMaximumDailyOrdersAfterPlacing(OrderId order) {
        return countToday() + 1 == MAX_NUMBER_OF_DAILY_HOLDS;
    }
}
