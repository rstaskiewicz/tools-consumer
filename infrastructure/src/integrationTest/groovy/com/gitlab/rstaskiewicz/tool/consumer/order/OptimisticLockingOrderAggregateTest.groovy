package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest
import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootIsStale
import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder

class OptimisticLockingOrderAggregateTest extends IntegrationTest {

    def 'optimistic locking on order aggregate should work'() {
        given:
            PlacedOrder placedOrder = placedOrder()
        and:
            orders.save(placedOrder)
        and:
            Order loadedOrder = loadPersistedOrder()
        and:
            someoneModifiedOrderInTheMeantime(placedOrder)
        when:
            orders.save(loadedOrder)
        then:
            thrown(AggregateRootIsStale)
            loadPersistedOrder().version == new Version(1)
    }

    void someoneModifiedOrderInTheMeantime(PlacedOrder placedOrder) {
        CancelledOrder cancelledOrder = placedOrder.handle(orderCanceled())
        orders.save(cancelledOrder)
    }
}
