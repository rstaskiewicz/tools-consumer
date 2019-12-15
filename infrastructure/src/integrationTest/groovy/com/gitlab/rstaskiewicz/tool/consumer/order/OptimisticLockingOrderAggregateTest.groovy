package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootIsStale
import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.control.Option
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
class OptimisticLockingOrderAggregateTest extends Specification {

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()

    @Autowired
    Orders orders

    def 'optimistic locking on order aggregate should work'() {
        given:
            PlacedOrder placedOrder = placedOrder(orderId, salesBranchId)
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

    Order loadPersistedOrder() {
        Option<Order> loaded = orders.findBy(orderId)
        return loaded.getOrElseThrow({ new IllegalStateException("should have been persisted") })
    }

    void someoneModifiedOrderInTheMeantime(PlacedOrder placedOrder) {
        CancelledOrder cancelledOrder = placedOrder.handle(orderCanceled())
        orders.save(cancelledOrder)
    }

    ConsumerEvent.OrderCanceled orderCanceled() {
        return ConsumerEvent.OrderCanceled.now(consumerId, orderId, salesBranchId, CancellationReason.ByConsumer)
    }
}
