package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootNotFoundException
import com.gitlab.rstaskiewicz.tool.consumer.common.commands.Result
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancelOrderCommand
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancelingOrder
import com.gitlab.rstaskiewicz.tool.consumer.consumer.Consumer
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.consumer.Consumers
import com.gitlab.rstaskiewicz.tool.consumer.consumer.FindPlacedOrder
import com.gitlab.rstaskiewicz.tool.consumer.consumer.OrderCancellingReason
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import io.vavr.control.Option
import io.vavr.control.Try
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.*
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder

class CancellingOrderSpec extends Specification {

    PlacedOrder placedOrder = placedOrder()
    ConsumerId consumerId = anyConsumerId()

    FindPlacedOrder willFindOrder = { orderId, ConsumerId -> Option.of(placedOrder) }
    FindPlacedOrder willNotFindOrder = { orderId, ConsumerId -> Option.none() }
    Consumers consumers = Stub()

    def 'should successfully cancel order if order was placed by consumer and consumer and order exist'() {
        given:
            CancelingOrder cancelingOrder = new CancelingOrder(willFindOrder, consumers)
        and:
            persistedConsumerWithPlacedOrder()
        when:
            Try<Result> result = cancelingOrder.cancel(command())
        then:
            result.isSuccess()
            result.get() == Result.Success
    }

    def 'should reject placing an order if one of the domain rules is broken (but should not fail)'() {
        given:
            CancelingOrder cancelingOrder = new CancelingOrder(willFindOrder, consumers)
        and:
            persistedConsumerWithoutPlacedOrders()
        when:
            Try<Result> result = cancelingOrder.cancel(command())
        then:
            result.isSuccess()
            result.get() == Result.Rejection
    }

    def 'should fail if consumer does not exists'() {
        given:
            CancelingOrder cancelingOrder = new CancelingOrder(willFindOrder, consumers)
        and:
            unknownConsumer()
        when:
            Try<Result> result = cancelingOrder.cancel(command())
        then:
            result.isFailure()
    }

    def 'should fail if order does not exists'() {
        given:
            CancelingOrder cancelingOrder = new CancelingOrder(willNotFindOrder, consumers)
        and:
            persistedConsumerWithPlacedOrder()
        when:
            Try<Result> result = cancelingOrder.cancel(command())
        then:
            result.isFailure()
    }

    def 'should fail if saving consumer fails'() {
        given:
            CancelingOrder cancelingOrder = new CancelingOrder(willFindOrder, consumers)
        and:
            persistedConsumerThatFailsOnSaving()
        when:
            Try<Result> result = cancelingOrder.cancel(command())
        then:
            result.isFailure()
    }

    CancelOrderCommand command() {
        return new CancelOrderCommand(consumerId, anyOrderId(), OrderCancellingReason.CancelledByConsumer)
    }

    void persistedConsumerWithPlacedOrder() {
        Consumer consumer = consumerWithPlacedOrder(placedOrder)
        consumers.findBy(consumerId) >> Option.of(consumer)
        consumers.publish(_ as ConsumerEvent) >> consumer
    }

    void persistedConsumerWithoutPlacedOrders() {
        Consumer consumer = consumer()
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
