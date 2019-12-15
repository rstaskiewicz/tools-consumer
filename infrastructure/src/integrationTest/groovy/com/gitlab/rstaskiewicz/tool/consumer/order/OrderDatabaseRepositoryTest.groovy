package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.placedOrder
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

@SpringBootTest
@Sql("/create_order_table.sql")
class OrderDatabaseRepositoryTest extends Specification {

    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()

    @Autowired
    Orders orders

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
