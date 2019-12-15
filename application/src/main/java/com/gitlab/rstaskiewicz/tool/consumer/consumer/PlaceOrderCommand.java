package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.NumberOfDays.of;
import static lombok.AccessLevel.PRIVATE;

@Value
public class PlaceOrderCommand {

    @NonNull ConsumerId consumerId;
    @NonNull SalesBranchId salesBranchId;
    @NonNull OrderId orderId;
    @NonNull @Getter(PRIVATE) Instant orderedAt;
    @NonNull @Getter(PRIVATE) Integer daysToDeadline;

    OrderTime getOrderTime() {
        return new OrderTime(orderedAt);
    }

    PaymentDeadline getPaymentDeadline() {
        return PaymentDeadline.forNumberOfDays(of(daysToDeadline), getOrderTime());
    }
}
