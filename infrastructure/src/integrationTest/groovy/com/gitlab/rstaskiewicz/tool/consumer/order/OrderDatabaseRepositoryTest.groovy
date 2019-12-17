package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest
import io.vavr.control.Option

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder

class OrderDatabaseRepositoryTest extends IntegrationTest {

    def 'persistence in real database should work'() {
        given:
            PlacedOrder placedOrder = placedOrder(orderId, salesBranchId)
        when:
            orders.save(placedOrder)
        then:
            bookIsPersistedAsPlacedOrder()
    }

    void bookIsPersistedAsPlacedOrder() {
        Order order = loadPersistedOrder()
        assert order.class == PlacedOrder
    }

    Order loadPersistedOrder() {
        Option<Order> loaded = orders.findBy(orderId)
        return loaded.getOrElseThrow({ new IllegalStateException("should have been persisted") })
    }
}
