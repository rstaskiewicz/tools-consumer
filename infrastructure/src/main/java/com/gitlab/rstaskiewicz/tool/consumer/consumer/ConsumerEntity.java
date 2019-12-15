package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlaced;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents;
import io.vavr.API;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@NoArgsConstructor
class ConsumerEntity {

    @Id
    Long id;
    UUID consumerId;
    Set<ConsumerOrderEntity> consumerOrders;
    Set<OverduePaymentEntity> overduePayments;

    ConsumerEntity(ConsumerId consumerId) {
        this.consumerId = consumerId.getId();
        this.consumerOrders = new HashSet<>();
        this.overduePayments = new HashSet<>();
    }

    ConsumerEntity handle(ConsumerEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(OrderPlacedEvents.class)), this::handle),
                Case($(instanceOf(OrderPlaced.class)), this::handle),
                Case($(instanceOf(OrderCanceled.class)), this::handle));
    }

    private ConsumerEntity handle(OrderPlacedEvents events) {
        OrderPlaced orderPlaced = events.getOrderPlaced();
        return handle(orderPlaced);
    }

    private ConsumerEntity handle(OrderPlaced event) {
        consumerOrders.add(new ConsumerOrderEntity(
                event.getOrderId(),
                event.getConsumerId(),
                event.getSalesBranchId(),
                event.getOrderedAt(),
                event.getPaymentTill()));
        return this;
    }

    private ConsumerEntity handle(OrderCanceled event) {
        return removeOrderIfPresent(consumerId, event.getOrderId(), event.getSalesBranchId());
    }

    private ConsumerEntity removeOrderIfPresent(UUID consumerId, UUID orderId, UUID salesBranchId) {
        consumerOrders.stream()
                .filter(entity -> entity.is(consumerId, orderId, salesBranchId))
                .findFirst()
                .ifPresent(entity -> consumerOrders.remove(entity));
        return this;
    }
}
