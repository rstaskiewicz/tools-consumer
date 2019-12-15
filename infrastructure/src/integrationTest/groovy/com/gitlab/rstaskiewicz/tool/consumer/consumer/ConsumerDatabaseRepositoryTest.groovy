package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.actualTime
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyPaymentDeadline
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.regularConsumer
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

@SpringBootTest
@Sql("/create_consumer_tables.sql")
class ConsumerDatabaseRepositoryTest extends Specification {

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()
    OrderTime orderTime = actualTime()
    PaymentDeadline paymentDeadline = anyPaymentDeadline()

    @Autowired
    Consumers consumers

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

    ConsumerEvent.ConsumerCreated consumerCreated() {
        return ConsumerEvent.ConsumerCreated.now(consumerId)
    }

    ConsumerEvent.OrderPlaced orderPlaced() {
        return ConsumerEvent.OrderPlaced.now(consumerId, orderId, salesBranchId, orderTime, paymentDeadline)
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

    Consumer loadPersistedConsumer() {
        Option<Consumer> loaded = consumers.findBy(consumerId)
        return loaded.getOrElseThrow({ new IllegalStateException("should have been persisted") })
    }
}
