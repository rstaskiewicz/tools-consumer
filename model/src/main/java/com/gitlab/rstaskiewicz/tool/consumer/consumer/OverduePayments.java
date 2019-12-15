package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;


@Value
class OverduePayments {

    static int MAX_COUNT_OF_OVERDUE_PAYMENTS_AT_BRANCH = 2;

    @NonNull Map<SalesBranchId, Set<OrderId>> overduePayments;

    int countAt(@NonNull SalesBranchId salesBranchId) {
        return overduePayments.getOrDefault(salesBranchId, emptySet()).size();
    }
}
