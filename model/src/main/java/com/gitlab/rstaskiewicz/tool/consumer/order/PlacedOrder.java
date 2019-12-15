package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.Instant;

import static lombok.AccessLevel.PACKAGE;

@Value
@EqualsAndHashCode(of = "orderId")
@RequiredArgsConstructor(access = PACKAGE)
public class PlacedOrder implements Order {

    @NonNull OrderId orderId;

    @NonNull ConsumerId byConsumer;

    @NonNull SalesBranchId placedAt;

    @NonNull Instant paymentTill;

    @NonNull Version version;

    public CancelledOrder handle(OrderCanceled event) {
        return new CancelledOrder(orderId, byConsumer, placedAt, event.getReason(), version);
    }
}
