package com.gitlab.rstaskiewicz.tool.consumer

import com.gitlab.rstaskiewicz.tool.consumer.consumer.Consumer
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId
import com.gitlab.rstaskiewicz.tool.consumer.consumer.Consumers
import com.gitlab.rstaskiewicz.tool.consumer.order.Order
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId
import com.gitlab.rstaskiewicz.tool.consumer.order.Orders
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification

import java.time.Instant

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason.ByConsumer
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static java.time.Instant.now
import static java.util.UUID.randomUUID

@SpringBootTest
@Sql([
        "/create_event_descriptor_table.sql",
        "/create_consumer_tables.sql",
        "/create_order_table.sql"
])
class IntegrationTest extends Specification {

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()
    Instant orderTime = now()
    Instant paymentDeadline = now().plusSeconds(3 * 24 * 3600)

    @Autowired
    Consumers consumers

    @Autowired
    Orders orders

    void consumerPersistedInDatabase() {
        consumers.publish(consumerCreated())
    }

    ConsumerEvent.ConsumerCreated consumerCreated() {
        return new ConsumerEvent.ConsumerCreated(randomUUID(), now(), consumerId.id)
    }

    ConsumerEvent.OrderPlaced orderPlaced() {
        return new ConsumerEvent.OrderPlaced(randomUUID(), now(), consumerId.id, orderId.id, salesBranchId.id, orderTime, paymentDeadline)
    }

    ConsumerEvent.OrderCanceled orderCanceled() {
        return new ConsumerEvent.OrderCanceled(randomUUID(), now(), consumerId.id, orderId.id, salesBranchId.id, ByConsumer)
    }

    Consumer loadPersistedConsumer() {
        Option<Consumer> loaded = consumers.findBy(consumerId)
        return loaded.getOrElseThrow({ new IllegalStateException('should have been persisted') })
    }

    Order loadPersistedOrder() {
        Option<Order> loaded = orders.findBy(orderId)
        return loaded.getOrElseThrow({ new IllegalStateException("should have been persisted") })
    }
}
