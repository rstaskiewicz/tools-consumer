package com.gitlab.rstaskiewicz.tool.consumer.consumer

import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId

import java.time.Instant
import java.util.stream.IntStream

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.PlacingOrderPolicy.allCurrentPolicies
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderFixture.anyOrderId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId
import static java.time.LocalDate.now
import static java.util.Collections.emptyMap
import static java.util.stream.Collectors.toSet

class ConsumerFixture {

    static Consumer regularConsumer() {
        return regularConsumer(anyConsumerId())
    }

    static Consumer regularConsumer(ConsumerId consumerId) {
        return new Consumer(
                consumerInformation(consumerId),
                new OverduePayments(emptyMap()),
                new ConsumerOrders(emptyMap()),
                allCurrentPolicies())
    }

    static Consumer consumerWith(ConsumerOrder order) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                new OverduePayments(emptyMap()),
                new ConsumerOrders(Map.of(now(), Set.of(order))),
                allCurrentPolicies())
    }

    static Consumer consumerWithMaxNumberOfPlacedOrdersPerDay() {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                new OverduePayments(emptyMap()),
                placedOrders(ConsumerOrders.MAX_NUMBER_OF_DAILY_HOLDS),
                allCurrentPolicies())
    }

    static Consumer consumerWithPlacedOrders(int numberOfOrders) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                new OverduePayments(emptyMap()),
                placedOrders(numberOfOrders),
                allCurrentPolicies())
    }

    static ConsumerOrders placedOrders(int numberOfOrders) {
        return new ConsumerOrders(Map.of(now(), IntStream.rangeClosed(1, numberOfOrders)
                .mapToObj({ i -> new ConsumerOrder(anyOrderId(), anyBranchId()) })
                .collect(toSet())))
    }

    static Consumer consumerWithOverduePaymentsAt(SalesBranchId salesBranchId, int numberOfOverduePayments) {
        return new Consumer(
                consumerInformation(anyConsumerId()),
                overduePaymentsAt(salesBranchId, numberOfOverduePayments),
                new ConsumerOrders(emptyMap()),
                allCurrentPolicies())
    }

    static OverduePayments overduePaymentsAt(SalesBranchId salesBranchId, int numberOfOverduePayments) {
        return new OverduePayments(HashMap.of(salesBranchId, IntStream.rangeClosed(1, numberOfOverduePayments)
                .mapToObj({ i -> anyOrderId() })
                .collect(toSet())))
    }

    static Consumer consumerWithPlacedOrder(PlacedOrder placedOrder) {
        return consumerWith(new ConsumerOrder(placedOrder.orderId, placedOrder.placedAt))
    }

    static ConsumerId anyConsumerId() {
        return consumerId(UUID.randomUUID())
    }

    static PaymentDeadline paymentDeadlineFor(NumberOfDays days, OrderTime orderedTime) {
        return PaymentDeadline.forNumberOfDays(days, orderedTime)
    }

    static PaymentDeadline anyPaymentDeadline() {
        return PaymentDeadline.forNumberOfDays(NumberOfDays.of(2), actualTime())
    }

    static OrderTime actualTime() {
        return new OrderTime(Instant.now())
    }

    private static ConsumerInformation consumerInformation(ConsumerId consumerId) {
        return new ConsumerInformation(consumerId)
    }

    private static ConsumerId consumerId(UUID consumerId) {
        return new ConsumerId(consumerId)
    }
}
