package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Cancelled;
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Paid;
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Placed;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static lombok.AccessLevel.PRIVATE;

@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
class OrderEntity {

    enum OrderState {
        Placed, Cancelled, Paid
    }

    UUID orderId;
    OrderState orderState;
    UUID placedByConsumer;
    UUID placedAtBranch;
    Instant paymentTill;
    Instant paidWhen;
    CancellationReason cancelledWhy;
    int version;

    Order toDomainModel() {
        return Match(orderState).of(
                Case($(Placed), this::toPlaceOrder),
                Case($(Paid), this::toPaidOrder),
                Case($(Cancelled), this::toCancelledOrder)
        );
    }

    private PlacedOrder toPlaceOrder() {
        return new PlacedOrder(
                new OrderId(orderId),
                new ConsumerId(placedByConsumer),
                new SalesBranchId(placedAtBranch),
                paymentTill,
                new Version(version));
    }

    private PaidOrder toPaidOrder() {
        return new PaidOrder(
                new OrderId(orderId),
                new ConsumerId(placedByConsumer),
                new SalesBranchId(placedAtBranch),
                paidWhen,
                new Version(version));
    }

    private CancelledOrder toCancelledOrder() {
        return new CancelledOrder(
                new OrderId(orderId),
                new ConsumerId(placedByConsumer),
                new SalesBranchId(placedAtBranch),
                cancelledWhy,
                new Version(version));
    }
}
