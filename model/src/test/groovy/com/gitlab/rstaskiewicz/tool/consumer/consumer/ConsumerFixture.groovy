package com.gitlab.rstaskiewicz.tool.consumer.consumer


import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId
import io.vavr.collection.HashMap
import io.vavr.collection.HashSet

import java.util.stream.IntStream

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.PlacingOrderPolicy.allCurrentPolicies
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static io.vavr.collection.HashMap.of
import static java.time.LocalDate.now

class ConsumerFixture {

    static Consumer consumer() {
        return consumer(anyConsumerId())
    }

    static Consumer consumer(ConsumerId consumerId) {
        return new Consumer(
                consumerInformation(consumerId),
                new OverduePayments(HashMap.empty()),
                new ConsumerOrders(HashMap.empty()),
                allCurrentPolicies())
    }

    static Consumer consumerWith(ConsumerOrder order) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                new OverduePayments(HashMap.empty()),
                new ConsumerOrders(of(now(), HashSet.of(order))),
                allCurrentPolicies())
    }

    static Consumer consumerWithPlacedOrders(int numberOfOrders) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                new OverduePayments(HashMap.empty()),
                placedOrders(numberOfOrders),
                allCurrentPolicies())
    }

    static ConsumerOrders placedOrders(int numberOfOrders) {
        return new ConsumerOrders(of(now(), IntStream.rangeClosed(1, numberOfOrders)
                .mapToObj({ i -> new ConsumerOrder(anyOrderId(), anyBranchId()) })
                .collect(HashSet.collector())))
    }

    static Consumer consumerWithOverduePaymentsAt(SalesBranchId salesBranchId, int numberOfOverduePayments) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                overduePaymentsAt(salesBranchId, numberOfOverduePayments),
                new ConsumerOrders(HashMap.empty()),
                allCurrentPolicies())
    }

    static OverduePayments overduePaymentsAt(SalesBranchId salesBranchId, int numberOfOverduePayments) {
        return new OverduePayments(of(salesBranchId, IntStream.rangeClosed(1, numberOfOverduePayments)
                .mapToObj({ i -> anyOrderId() })
                .collect(HashSet.collector())))
    }

    static Consumer consumerWithPlacedOrder(PlacedOrder placedOrder) {
        return consumerWith(new ConsumerOrder(placedOrder.orderId, placedOrder.salesBranchId))
    }

    static ConsumerId anyConsumerId() {
        return consumerId(UUID.randomUUID())
    }

    static PaymentDeadline anyPaymentDeadline() {
        return PaymentDeadline.forNumberOfDays(NumberOfDays.of(2))
    }

    private static ConsumerInformation consumerInformation(ConsumerId consumerId) {
        return new ConsumerInformation(consumerId)
    }

    private static ConsumerId consumerId(UUID consumerId) {
        return new ConsumerId(consumerId)
    }
}
