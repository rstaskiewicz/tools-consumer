package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public class ConsumerFactory {

    public Consumer create(ConsumerId consumerId,
                           Map<LocalDate, Set<ConsumerOrder>> consumerOrders,
                           Map<SalesBranchId, Set<OrderId>> overduePayments) {
        return new Consumer(new ConsumerInformation(consumerId),
                new OverduePayments(overduePayments),
                new ConsumerOrders(consumerOrders),
                PlacingOrderPolicy.allCurrentPolicies());
    }
}
