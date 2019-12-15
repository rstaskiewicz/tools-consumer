package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;

public interface Order {

    OrderId getOrderId();

    Version getVersion();

    ConsumerId getByConsumer();

    SalesBranchId getPlacedAt();
}
