package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.consumer.FindPlacedOrder
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

@SpringBootTest
@Sql("/create_order_table.sql")
class FindOrderInDatabaseTest extends Specification {

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()

    @Autowired
    Orders orders

    @Autowired
    FindPlacedOrder findPlacedOrder

    def 'should find placed order in database'() {
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

    ConsumerEvent.OrderCanceled orderCanceled() {
        return ConsumerEvent.OrderCanceled.now(consumerId, orderId, salesBranchId, CancellationReason.ByConsumer)
    }
}
