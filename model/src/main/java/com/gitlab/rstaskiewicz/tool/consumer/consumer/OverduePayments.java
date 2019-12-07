package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.NonNull;
import lombok.Value;


@Value
class OverduePayments {

    static int MAX_COUNT_OF_OVERDUE_PAYMENTS_AT_BRANCH = 2;

    @NonNull Map<SalesBranchId, Set<OrderId>> overduePayments;

    int countAt(@NonNull SalesBranchId salesBranchId) {
        return overduePayments.getOrElse(salesBranchId, HashSet.empty()).size();
    }
}
