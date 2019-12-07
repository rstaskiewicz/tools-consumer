package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerOrders.MAX_NUMBER_OF_DAILY_HOLDS;
import static com.gitlab.rstaskiewicz.tool.consumer.consumer.OverduePayments.MAX_COUNT_OF_OVERDUE_PAYMENTS_AT_BRANCH;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

interface PlacingOrderPolicy extends Function2<Consumer, SalesBranchId, Either<Rejection, Allowance>> {

    PlacingOrderPolicy overduePaymentsRejection = (Consumer consumer, SalesBranchId salesBranchId) -> {
        if (consumer.overduePaymentsAt(salesBranchId) >= MAX_COUNT_OF_OVERDUE_PAYMENTS_AT_BRANCH) {
            return left(Rejection.withReason(("Consumer cannot place order in sales branch when there are overdue payments")));
        }
        return right(new Allowance());
    };

    PlacingOrderPolicy maximumNumberOfDailyOrders = (Consumer consumer, SalesBranchId salesBranchId) -> {
        if (consumer.numberOfOrdersPlacedToday() >= MAX_NUMBER_OF_DAILY_HOLDS) {
            return left(Rejection.withReason(("Consumer cannot place more orders today")));
        }
        return right(new Allowance());
    };

    static List<PlacingOrderPolicy> allCurrentPolicies() {
        return List.of(
                overduePaymentsRejection,
                maximumNumberOfDailyOrders);
    }
}

@Value
class Allowance { }

@Value
class Rejection {

    @NonNull
    Reason reason;

    static Rejection withReason(String reason) {
        return new Rejection(new Reason(reason));
    }

    @Value
    static class Reason {
        @NonNull
        String reason;
    }
}
