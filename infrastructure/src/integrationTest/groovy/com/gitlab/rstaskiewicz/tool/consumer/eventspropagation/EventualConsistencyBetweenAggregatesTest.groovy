package com.gitlab.rstaskiewicz.tool.consumer.eventspropagation

import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest
import com.gitlab.rstaskiewicz.tool.consumer.consumer.Consumer
import com.gitlab.rstaskiewicz.tool.consumer.order.CancelledOrder
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import spock.util.concurrent.PollingConditions

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.regularConsumer

class EventualConsistencyBetweenAggregatesTest extends IntegrationTest {

    PollingConditions pollingConditions = new PollingConditions(timeout: 5)

    def 'Consumer and Order aggregates should be synchronized through events'() {
        given:
            consumerPersistedInDatabase()
        when:
            consumers.publish(orderPlaced())
        then:
            consumerShouldBeFoundInDatabaseWithOnePlacedOrder()
        and:
            orderShouldBePlacedInDatabaseOnEvent()
        when:
            consumers.publish(orderCanceled())
        then:
            consumerShouldBeFoundInDatabaseWithoutPlacedOrders()
        and:
            orderShouldReactToEvent()
    }

    void consumerShouldBeFoundInDatabaseWithOnePlacedOrder() {
        Consumer consumer = loadPersistedConsumer()
        assert consumer.numberOfOrdersPlacedToday() == 1
        assertConsumerInformation(consumer)
    }

    void orderShouldBePlacedInDatabaseOnEvent() {
        pollingConditions.eventually {
            assert orders.findBy(orderId).get() instanceof PlacedOrder
        }
    }

    void consumerShouldBeFoundInDatabaseWithoutPlacedOrders() {
        Consumer consumer = loadPersistedConsumer()
        assert consumer.numberOfOrdersPlacedToday() == 0
        assertConsumerInformation(consumer)
    }

    void orderShouldReactToEvent() {
        pollingConditions.eventually {
            assert orders.findBy(orderId).get() instanceof CancelledOrder
        }
    }

    void assertConsumerInformation(Consumer consumer) {
        assert consumer == regularConsumer(consumerId)
    }
}
