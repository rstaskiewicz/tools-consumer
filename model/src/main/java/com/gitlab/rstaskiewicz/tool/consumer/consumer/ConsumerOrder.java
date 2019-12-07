package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.NonNull;
import lombok.Value;

@Value
class ConsumerOrder {
    @NonNull OrderId orderId;
    @NonNull SalesBranchId salesBranchId;
}
