package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import spock.lang.Specification

import java.time.Instant

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason.ByConsumer
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Cancelled
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Paid
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Placed
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static java.time.Instant.now

class OrderEntityToDomainModelMappingSpec extends Specification {

    OrderId orderId = anyOrderId()
    ConsumerId consumerId = anyConsumerId()
    SalesBranchId salesBranchId = anyBranchId()
    Instant paymentTill = now().plusSeconds(3 * 24 * 3600)
    Instant paidWhen = now().plusSeconds(2 * 24 * 3600)
    CancellationReason cancellationReason = ByConsumer

    def 'should map to placed order'() {
        given:
            OrderEntity entity = orderEntity(Placed)
        when:
            Order order = entity.toDomainModel()
        and:
            PlacedOrder placedOrder = order as PlacedOrder
        then:
            placedOrder.orderId == orderId
            placedOrder.byConsumer == consumerId
            placedOrder.placedAt == salesBranchId
            placedOrder.paymentTill == paymentTill
    }

    def 'should map to paid order'() {
        given:
            OrderEntity entity = orderEntity(Paid)
        when:
            Order order = entity.toDomainModel()
        and:
            PaidOrder paidOrder = order as PaidOrder
        then:
            paidOrder.orderId == orderId
            paidOrder.byConsumer == consumerId
            paidOrder.placedAt == salesBranchId
            paidOrder.paidWhen  == paidWhen
    }

    def 'should map to cancelled order'() {
        given:
            OrderEntity entity = orderEntity(Cancelled)
        when:
            Order order = entity.toDomainModel()
        and:
            CancelledOrder cancelledOrder = order as CancelledOrder
        then:
            cancelledOrder.orderId == orderId
            cancelledOrder.byConsumer == consumerId
            cancelledOrder.placedAt == salesBranchId
            cancelledOrder.cancelledWhy  == cancellationReason
    }

    OrderEntity orderEntity(OrderState state) {
        new OrderEntity(
                orderId: orderId.getId(),
                orderState: state,
                placedByConsumer: consumerId.getId(),
                placedAtBranch: salesBranchId.getId(),
                paymentTill: paymentTill,
                paidWhen: paidWhen,
                cancelledWhy: cancellationReason)
    }
}
