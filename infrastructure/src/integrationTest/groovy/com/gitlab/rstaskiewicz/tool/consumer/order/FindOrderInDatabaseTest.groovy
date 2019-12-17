package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest
import com.gitlab.rstaskiewicz.tool.consumer.consumer.FindPlacedOrder
import org.springframework.beans.factory.annotation.Autowired

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder

class FindOrderInDatabaseTest extends IntegrationTest {

    @Autowired
    FindPlacedOrder findPlacedOrder

    def 'placed order should be found in database'() {
        given:
            PlacedOrder placedOrder = placedOrder(orderId, salesBranchId)
        when:
            orders.save(placedOrder)
        then:
            findPlacedOrder.findPlacedOrder(orderId).isDefined()
        when:
            CancelledOrder cancelledOrder = placedOrder.handle(orderCanceled())
        and:
            orders.save(cancelledOrder)
        then:
            findPlacedOrder.findPlacedOrder(orderId).isEmpty()
    }
}
