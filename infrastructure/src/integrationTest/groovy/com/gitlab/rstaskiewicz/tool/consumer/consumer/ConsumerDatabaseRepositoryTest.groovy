package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.regularConsumer

class ConsumerDatabaseRepositoryTest extends IntegrationTest {

    def 'persistence in real database should work'() {
        when:
            consumers.publish(consumerCreated())
        then:
            consumerShouldBeFoundInDatabaseWithoutPlacedOrders()
        when:
            consumers.publish(orderPlaced())
        then:
            consumerShouldBeFoundInDatabaseWithOnePlacedOrder()
    }

    void consumerShouldBeFoundInDatabaseWithoutPlacedOrders() {
        Consumer consumer = loadPersistedConsumer()
        assert consumer.numberOfOrdersPlacedToday() == 0
        assertConsumerInformation(consumer)
    }

    void consumerShouldBeFoundInDatabaseWithOnePlacedOrder() {
        Consumer consumer = loadPersistedConsumer()
        assert consumer.numberOfOrdersPlacedToday() == 1
        assertConsumerInformation(consumer)
    }

    void assertConsumerInformation(Consumer consumer) {
        assert consumer == regularConsumer(consumerId)
    }
}
