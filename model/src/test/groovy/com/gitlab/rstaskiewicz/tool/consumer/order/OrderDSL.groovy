package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId

import java.time.Instant

import static com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version.zero

class OrderDSL {

    private static final THREE_DAYS_FROM_NOW = Instant.now().plusSeconds(3 * 24 * 3600)

    OrderId orderId
    ConsumerId consumerId
    SalesBranchId salesBranchId
    Version version = zero()

    Closure<Order> orderProvider

    static OrderDSL the(OrderDSL order) {
        return order
    }

    static OrderDSL order() {
        return new OrderDSL()
    }

    OrderDSL() { }

    OrderDSL with(OrderId orderId) {
        this.orderId = orderId
        return this
    }

    OrderDSL placedAt(SalesBranchId salesBranchId) {
        this.salesBranchId = salesBranchId
        return this
    }

    OrderDSL placedBy(ConsumerId consumerId) {
        this.consumerId = consumerId
        this.orderProvider = { ->
            new PlacedOrder(orderId, consumerId, salesBranchId, THREE_DAYS_FROM_NOW, version)
        }
        return this
    }

    ConsumerEvent.OrderCanceled isCancelledWith(CancellationReason reason) {
        return orderCanceled(orderProvider(), reason)
    }

    Order reactsTo(ConsumerEvent event) {
        return orderProvider().handle(event)
    }

    private static ConsumerEvent.OrderCanceled orderCanceled(PlacedOrder placedOrder, CancellationReason reason) {
        return ConsumerEvent.OrderCanceled.now(placedOrder.byConsumer, placedOrder.orderId, placedOrder.placedAt, reason)
    }
}
