package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import spock.lang.Specification

import java.time.Duration

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents.events
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.actualTime
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.paymentDeadlineFor
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class CreatingDataModelFromConsumerEventsSpec extends Specification {

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()
    OrderTime orderedAt = actualTime()

    def 'should add order on OrderPlaced event with payment deadline'() {
        given:
            ConsumerEntity entity = consumerEntity()
        when:
            entity.handle(orderPlacedWithPaymentDeadline(NumberOfDays.of(2)))
        then:
            entity.consumerOrders.size() == 1
            entity.consumerOrders.iterator().next().paymentTill == orderedAt.when.plus(Duration.ofDays(2))
    }

    def 'should remove order on OrderCancelled event'() {
        given:
            ConsumerEntity entity = consumerEntity()
        when:
            entity.handle(orderPlacedWithPaymentDeadline())
        then:
            entity.consumerOrders.size() == 1
        when:
            entity.handle(orderCanceled())
        then:
            entity.consumerOrders.size() == 0
    }

    ConsumerEntity consumerEntity() {
        return new ConsumerEntity(consumerId)
    }

    ConsumerEvent.OrderPlacedEvents orderPlacedWithPaymentDeadline(NumberOfDays days = NumberOfDays.of(3)) {
        return events(ConsumerEvent.OrderPlaced.now(
                consumerId,
                orderId,
                salesBranchId,
                orderedAt,
                paymentDeadlineFor(days, orderedAt)))
    }

    ConsumerEvent.OrderCanceled orderCanceled() {
        return ConsumerEvent.OrderCanceled.now(consumerId, orderId, salesBranchId, CancellationReason.ByConsumer)
    }
}
