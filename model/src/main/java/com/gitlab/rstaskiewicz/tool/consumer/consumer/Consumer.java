package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.MaximumNumberOfDailyOrdersReached;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCancelingFailed;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlaced;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacingFailed;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.gitlab.rstaskiewicz.tool.consumer.common.event.EitherResult.announceFailure;
import static com.gitlab.rstaskiewicz.tool.consumer.common.event.EitherResult.announceSuccess;
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents.events;
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerOrders.MAX_NUMBER_OF_DAILY_HOLDS;
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.Rejection.withReason;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(of = "consumer")
@RequiredArgsConstructor(access = PACKAGE)
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class Consumer {

    @NonNull ConsumerInformation consumer;

    @NonNull OverduePayments overduePayments;

    @NonNull ConsumerOrders orders;

    @NonNull List<PlacingOrderPolicy> placingOrderPolicies;

    public Either<OrderPlacingFailed, OrderPlacedEvents> placeOrder(OrderId orderId,
                                                                    SalesBranchId salesBranchId,
                                                                    PaymentDeadline paymentDeadline) {
        var rejection = consumerCanPlaceOrder(salesBranchId);
        if (rejection.isEmpty()) {
            var orderPlaced = OrderPlaced.now(consumer.getConsumerId(), orderId, salesBranchId, paymentDeadline);
            if (orders.reachedMaximumDailyOrdersAfterPlacing(orderId)) {
                return announceSuccess(events(
                        orderPlaced, MaximumNumberOfDailyOrdersReached.now(consumer.getConsumerId(), MAX_NUMBER_OF_DAILY_HOLDS)));
            }
            return announceSuccess(events(orderPlaced));
        }
        return announceFailure(OrderPlacingFailed.now(rejection.get(), consumer.getConsumerId(), orderId, salesBranchId));
    }

    public Either<OrderCancelingFailed, OrderCanceled> cancelOrder(PlacedOrder order,
                                                                   OrderCancellingReason cancellingReason) {
        if (orders.contains(order)) {
            return announceSuccess(OrderCanceled.now(
                    consumer.getConsumerId(), order.getOrderId(), order.getSalesBranchId(), cancellingReason));
        }
        return announceFailure(OrderCancelingFailed.now(
                withReason("Order was not placed by consumer"),consumer.getConsumerId(), order.getOrderId(), order.getSalesBranchId()));
    }

    private Option<Rejection> consumerCanPlaceOrder(SalesBranchId salesBranchId) {
        return placingOrderPolicies
                .map(policy -> policy.apply(this, salesBranchId))
                .find(Either::isLeft)
                .map(Either::getLeft);
    }

    int overduePaymentsAt(SalesBranchId salesBranch) {
        return overduePayments.countAt(salesBranch);
    }

    int numberOfOrdersPlacedToday() {
        return orders.countToday();
    }
}
