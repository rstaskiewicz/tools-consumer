package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.control.Either
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacingFailed
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyPaymentDeadline
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.consumerWithOverduePaymentsAt
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.consumerWithPlacedOrders
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static java.util.Collections.emptySet

class ConsumerPlacingOrderSpec extends Specification {

    def 'consumer cannot place more than 3 orders in one day'() {
        given:
            Consumer consumer = consumerWithPlacedOrders(orders)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), anyBranchId(), anyPaymentDeadline())
        then:
            placeOrder.isLeft()
            placeOrder.getLeft().reason == 'Consumer cannot place more orders today'
        where:
            orders << [3, 6, 100]
    }

    def 'consumer can place an order when he did not place more than 2 orders in one day'() {
        given:
            Consumer consumer = consumerWithPlacedOrders(orders)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), anyBranchId(), anyPaymentDeadline())
        then:
            placeOrder.isRight()
        where:
            orders << [0, 1, 2]
    }

    def 'consumer cannot place orders in sales branch anymore when he has at least two overdue payments there'() {
        given:
            SalesBranchId salesBranchId = anyBranchId()
        and:
            Consumer consumer = consumerWithOverduePaymentsAt(salesBranchId, overduePayments)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), salesBranchId, anyPaymentDeadline())
        then:
            placeOrder.isLeft()
            placeOrder.getLeft().reason == 'Consumer cannot place order in sales branch when there are overdue payments'
        where:
            overduePayments << [2, 4, 1000]
    }

    def 'consumer can place an order even though he has 2 overdue checkouts at different sales branch'() {
        given:
            SalesBranchId salesBranchId = anyBranchId()
            SalesBranchId differentSalesBranchId = anyBranchId()
        and:
            Consumer consumer = consumerWithOverduePaymentsAt(salesBranchId, overduePayments)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), differentSalesBranchId, anyPaymentDeadline())
        then:
            placeOrder.isRight()
        where:
            overduePayments << [2, 4, 1000]
    }

    def 'consumer can place an order in sales branch when he does not have 2 overdue payments'() {
        given:
            SalesBranchId salesBranchId = anyBranchId()
        and:
            Consumer consumer = consumerWithOverduePaymentsAt(salesBranchId, overduePayments)
        when:
            Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder = consumer.placeOrder(
                    anyOrderId(), salesBranchId, anyPaymentDeadline())
        then:
            placeOrder.isRight()
        where:
            overduePayments << [0, 1]
    }
}
