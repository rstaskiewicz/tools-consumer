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

    default UUID getAggregateId() {
        return getConsumerId();
    }

    default List<ConsumerEvent> normalize() {
        return List.of(this);
    }

    @Value
    class ConsumerCreated implements ConsumerEvent {

        public static final String TYPE = "consumer-created";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull UUID consumerId;

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class OrderPlaced implements ConsumerEvent {

        public static final String TYPE = "order-placed";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;
        @NonNull Instant orderedAt;
        @NonNull Instant paymentTill;

        static OrderPlaced now(ConsumerId consumerId,
                               OrderId orderId,
                               SalesBranchId salesBranchId,
                               OrderTime orderTime,
                               PaymentDeadline paymentDeadline) {
            return new OrderPlaced(
                    UUID.randomUUID(),
                    Instant.now(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId(),
                    orderTime.getWhen(),
                    paymentDeadline.getTill());
        }

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class OrderPlacedEvents implements ConsumerEvent {

        public static final String TYPE = "order-placed-events";

        @NonNull UUID eventId;
        @NonNull UUID consumerId;
        @NonNull OrderPlaced orderPlaced;
        @NonNull Option<MaximumNumberOfDailyOrdersReached> maximumNumberOfDailyOrdersReached;

        @Override
        public Instant getWhen() {
            return orderPlaced.when;
        }

        static OrderPlacedEvents events(OrderPlaced orderPlaced) {
            return new OrderPlacedEvents(UUID.randomUUID(), orderPlaced.consumerId, orderPlaced, Option.none());
        }

        static OrderPlacedEvents events(OrderPlaced orderPlaced, MaximumNumberOfDailyOrdersReached maximumNumberOfDailyOrdersReached) {
            return new OrderPlacedEvents(UUID.randomUUID(), orderPlaced.consumerId, orderPlaced, Option.of(maximumNumberOfDailyOrdersReached));
        }

        public List<ConsumerEvent> normalize() {
            return List.<ConsumerEvent>of(orderPlaced).appendAll(maximumNumberOfDailyOrdersReached.toList());
        }

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class MaximumNumberOfDailyOrdersReached implements ConsumerEvent {

        public static final String TYPE = "max-num-of-daily-orders-reached";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull UUID consumerId;
        int numberOfOrders;

        static MaximumNumberOfDailyOrdersReached now(ConsumerId consumerId, int numberOfOrders) {
            return new MaximumNumberOfDailyOrdersReached(
                    UUID.randomUUID(),
                    Instant.now(),
                    consumerId.getId(),
                    numberOfOrders);
        }

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class OrderPlacingFailed implements ConsumerEvent {

        public static final String TYPE = "order-placing-failed";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull String reason;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;

        static OrderPlacingFailed now(Rejection rejection, ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId) {
            return new OrderPlacingFailed(
                    UUID.randomUUID(),
                    Instant.now(),
                    rejection.getReason().getReason(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId());
        }

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class OrderCanceled implements ConsumerEvent {

        public static final String TYPE = "order-cancelled";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;
        @NonNull CancellationReason reason;

        static OrderCanceled now(ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId, CancellationReason reason) {
            return new OrderCanceled(
                    UUID.randomUUID(),
                    Instant.now(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId(),
                    reason);
        }

        public String getType() {
            return TYPE;
        }
    }

    @Value
    class OrderCancelingFailed implements ConsumerEvent {

        public static final String TYPE = "order-cancelling-failed";

        @NonNull UUID eventId;
        @NonNull Instant when;
        @NonNull String reason;
        @NonNull UUID consumerId;
        @NonNull UUID orderId;
        @NonNull UUID salesBranchId;

        static OrderCancelingFailed now(Rejection rejection, ConsumerId consumerId, OrderId orderId, SalesBranchId salesBranchId) {
            return new OrderCancelingFailed(
                    UUID.randomUUID(),
                    Instant.now(),
                    rejection.getReason().getReason(),
                    consumerId.getId(),
                    orderId.getId(),
                    salesBranchId.getId());
        }

        public String getType() {
            return TYPE;
        }
    }
}
