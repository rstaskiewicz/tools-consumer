package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static java.time.Instant.now
import static java.util.Collections.emptyList

class ConsumerEntityToDomainModelMappingSpec extends Specification {

    DomainModelMapper domainModelMapper = new DomainModelMapper()

    ConsumerId consumerId = anyConsumerId()
    OrderId orderId = anyOrderId()
    OrderId anotherOrderId = anyOrderId()
    SalesBranchId salesBranchId = anyBranchId()
    SalesBranchId anotherSalesBranchId = anyBranchId()
    Instant orderedToday = now()
    Instant orderedYesterday = now().minusSeconds(24 * 3600)
    Instant paymentTill = now().plusSeconds(3 * 24 * 3600)

    LocalDate today = LocalDate.now()
    LocalDate yesterday = LocalDate.now().minusDays(1)

    def 'should map consumer orders'() {
        given:
            ConsumerEntity entity = consumerEntity([
                    new ConsumerOrderEntity(orderId.id, consumerId.id, salesBranchId.id, orderedToday, paymentTill),
                    new ConsumerOrderEntity(anotherOrderId.id, consumerId.id, anotherSalesBranchId.id, orderedYesterday, paymentTill)])
        when:
            Map<LocalDate, Set<ConsumerOrder>> consumerOrders = domainModelMapper.mapConsumerOrders(entity)
        then:
            consumerOrders.get(today).size() == 1
            consumerOrders.get(today).iterator().next().orderId == orderId
            consumerOrders.get(yesterday).size() == 1
            consumerOrders.get(yesterday).iterator().next().orderId == anotherOrderId
    }

    def 'should map overdue payments'() {
        given:
            ConsumerEntity entity = consumerEntity([], [
                    new OverduePaymentEntity(consumerId.id, orderId.id, salesBranchId.id),
                    new OverduePaymentEntity(consumerId.id, anotherOrderId.id, anotherSalesBranchId.id)])
        when:
            Map<SalesBranchId, Set<OrderId>> overduePayments = domainModelMapper.mapOverduePayments(entity)
        then:
            overduePayments.get(salesBranchId).size() == 1
            overduePayments.get(salesBranchId).contains(orderId)
            overduePayments.get(anotherSalesBranchId).size() == 1
            overduePayments.get(anotherSalesBranchId).contains(anotherOrderId)
    }

    ConsumerEntity consumerEntity(List<ConsumerOrderEntity> consumerOrders = emptyList(),
                                  List<OverduePaymentEntity> overduePayments = emptyList()) {
        ConsumerEntity entity = new ConsumerEntity(consumerId)
        entity.consumerOrders = consumerOrders as Set
        entity.overduePayments = overduePayments as Set
        return entity
    }
}
