package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderDSL.order
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderDSL.the
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class PlacedOrderCancellingTest extends Specification {

    def 'should make order cancelled when cancelling occur'() {
        given:
            OrderDSL placedOrder = order() with anyOrderId() placedAt anyBranchId() placedBy anyConsumerId()
        and:
            ConsumerEvent.OrderCanceled orderCanceledEvent = the placedOrder isCancelledWith CancellationReason.ByConsumer
        when:
            CancelledOrder cancelledOrder = the placedOrder reactsTo orderCanceledEvent
        then:
            cancelledOrder.orderId == placedOrder.orderId
            cancelledOrder.byConsumer == placedOrder.consumerId
            cancelledOrder.placedAt == placedOrder.salesBranchId
            cancelledOrder.cancelledWhy == CancellationReason.ByConsumer
            cancelledOrder.version == placedOrder.version
    }
}
