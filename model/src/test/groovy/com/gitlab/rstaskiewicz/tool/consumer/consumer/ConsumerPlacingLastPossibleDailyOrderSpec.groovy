package com.gitlab.rstaskiewicz.tool.consumer.consumer


import io.vavr.control.Either
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacingFailed
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyPaymentDeadline
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.consumerWithMaxNumberOfPlacedOrdersPerDay
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class ConsumerPlacingLastPossibleDailyOrderSpec extends Specification {

    def 'should announce that a customer places his last possible daily order (2th)'() {
        given:
            Consumer consumer = consumerWithMaxNumberOfPlacedOrdersPerDay(2)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), anyBranchId(), anyPaymentDeadline())
        then:
            placeOrder.isRight()
            placeOrder.get()with {
                assert it.maximumNumberOfDailyOrdersReached.isDefined()
                assert it.maximumNumberOfDailyOrdersReached.get().numberOfOrders == 3
            }
    }
}
