package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import io.vavr.control.Either
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.regularConsumer
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.consumerWithPlacedOrder
import static CancellationReason.ByConsumer
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCancelingFailed

class ConsumerCancellingOrderSpec extends Specification {

    def 'consumer should be able to cancel his order'() {
        given:
            PlacedOrder order = placedOrder()
        and:
            Consumer consumer = consumerWithPlacedOrder(order)
        when:
            Either<OrderCancelingFailed, OrderCanceled> cancelOrder = consumer.cancelOrder(order, ByConsumer)
        then:
            cancelOrder.isRight()
            cancelOrder.get().with {
                assert it.orderId == order.orderId.id
                assert it.salesBranchId == order.placedAt.id
            }
    }

    def 'consumer cannot cancel an order which does not exist'() {
        given:
            PlacedOrder order = placedOrder()
        and:
            Consumer consumer = regularConsumer()
        when:
            Either<OrderCancelingFailed, OrderCanceled> cancelOrder = consumer.cancelOrder(order, ByConsumer)
        then:
            cancelOrder.isLeft()
    }

    def 'consumer cannot cancel an order which was done by someone else'() {
        given:
            PlacedOrder order = placedOrder()
        and:
            Consumer consumer = regularConsumer()
        and:
            Consumer differentConsumer = consumerWithPlacedOrder(order)
        when:
            Either<OrderCancelingFailed, OrderCanceled> cancelOrder = consumer.cancelOrder(order, ByConsumer)
        then:
            cancelOrder.isLeft()
    }
}
