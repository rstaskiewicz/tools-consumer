package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootNotFoundException
import com.gitlab.rstaskiewicz.tool.consumer.common.commands.Result
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import io.vavr.control.Option
import io.vavr.control.Try
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.*
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class PlacingOrderSpec extends Specification {

    PlacedOrder placedOrder = placedOrder()
    ConsumerId consumerId = anyConsumerId()

    Consumers consumers = Stub()

    def 'should successfully place order if order was placed by consumer and consumer and order exist'() {
        given:
            PlacingOrder placingOrder = new PlacingOrder(consumers)
        and:
            persistedConsumerWithPlacedOrder()
        when:
            Try<Result> result = placingOrder.place(command())
        then:
            result.isSuccess()
            result.get() == Result.Success
    }

    def 'should reject placing an order if one of the domain rules is broken (but should not fail)'() {
        given:
            PlacingOrder placingOrder = new PlacingOrder(consumers)
        and:
            persistedConsumerWithMaxNumberOfPlacedOrdersPerDay()
        when:
            Try<Result> result = placingOrder.place(command())
        then:
            result.isSuccess()
            result.get() == Result.Rejection
    }

    def 'should fail if consumer does not exists'() {
        given:
            PlacingOrder placingOrder = new PlacingOrder(consumers)
        and:
            unknownConsumer()
        when:
            Try<Result> result = placingOrder.place(command())
        then:
            result.isFailure()
    }

    def 'should fail if saving consumer fails'() {
        given:
            PlacingOrder placingOrder = new PlacingOrder(consumers)
        and:
            persistedConsumerThatFailsOnSaving()
        when:
            Try<Result> result = placingOrder.place(command())
        then:
            result.isFailure()
    }

    PlaceOrderCommand command() {
        return new PlaceOrderCommand(consumerId, anyBranchId(), anyOrderId(), 3)
    }

    void persistedConsumerWithPlacedOrder() {
        Consumer consumer = consumerWithPlacedOrder(placedOrder)
        consumers.findBy(consumerId) >> Option.of(consumer)
        consumers.publish(_ as ConsumerEvent) >> consumer
    }

    void persistedConsumerWithMaxNumberOfPlacedOrdersPerDay() {
        Consumer consumer = consumerWithMaxNumberOfPlacedOrdersPerDay()
        consumers.findBy(consumerId) >> Option.of(consumer)
        consumers.publish(_ as ConsumerEvent) >> consumer
    }

    void persistedConsumerThatFailsOnSaving() {
        Consumer consumer = consumerWithPlacedOrder(placedOrder)
        consumers.findBy(consumerId) >> Option.of(consumer)
        consumers.publish(_ as ConsumerEvent) >> { throw new AggregateRootNotFoundException() }
    }

    ConsumerId unknownConsumer() {
        return anyConsumerId()
    }
}
