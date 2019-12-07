package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

public interface ConsumerEvent extends DomainEvent {

    UUID getConsumerId();

    default ConsumerId consumerId() {
        return new ConsumerId(getConsumerId());
    }

    default List<ConsumerEvent> normalize() {
        return List.of(this);
    }

    @Value
    class ConsumerCreated implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID consumerId;

        static ConsumerCreated now(ConsumerId consumerId) {
            return new ConsumerCreated(Instant.now(), consumerId.getId());
        }
    }

    @Value
    class OrderPlaced implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;
        @NonNull Instant paymentTill;

        static OrderPlaced now(ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId, PaymentDeadline paymentDeadline) {
            return new OrderPlaced(
                    Instant.now(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId(),
                    paymentDeadline.getWhen());
        }
    }

    @Value
    class OrderPlacedEvents implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull UUID consumerId;
        @NonNull OrderPlaced orderPlaced;
        @NonNull Option<MaximumNumberOfDailyOrdersReached> maximumNumberOfDailyOrdersReached;

        @Override
        public Instant getWhen() {
            return orderPlaced.when;
        }

        static OrderPlacedEvents events(OrderPlaced orderPlaced) {
            return new OrderPlacedEvents(orderPlaced.consumerId, orderPlaced, Option.none());
        }

        static OrderPlacedEvents events(OrderPlaced orderPlaced, MaximumNumberOfDailyOrdersReached maximumNumberOfDailyOrdersReached) {
            return new OrderPlacedEvents(orderPlaced.consumerId, orderPlaced, Option.of(maximumNumberOfDailyOrdersReached));
        }

        public List<ConsumerEvent> normalize() {
            return List.<ConsumerEvent>of(orderPlaced).appendAll(maximumNumberOfDailyOrdersReached.toList());
        }
    }

    @Value
    class MaximumNumberOfDailyOrdersReached implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID consumerId;
        int numberOfOrders;

        static MaximumNumberOfDailyOrdersReached now(ConsumerId consumerId, int numberOfOrders) {
            return new MaximumNumberOfDailyOrdersReached(
                    Instant.now(),
                    consumerId.getId(),
                    numberOfOrders);
        }
    }

    @Value
    class OrderPlacingFailed implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull String reason;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;

        static OrderPlacingFailed now(Rejection rejection, ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId) {
            return new OrderPlacingFailed(
                    Instant.now(),
                    rejection.getReason().getReason(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId());
        }
    }

    @Value
    class OrderCanceled implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;
        @NonNull OrderCancellingReason reason;

        static OrderCanceled now(ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId, OrderCancellingReason reason) {
            return new OrderCanceled(
                    Instant.now(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId(),
                    reason);
        }
    }

    @Value
    class OrderCancelingFailed implements ConsumerEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull String reason;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;

        static OrderCancelingFailed now(Rejection rejection, ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId) {
            return new OrderCancelingFailed(
                    Instant.now(),
                    rejection.getReason().getReason(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId());
        }
    }
}
