package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId

import java.time.Instant

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class OrderFixture {

    static PlacedOrder placedOrder() {
        return new PlacedOrder(anyOrderId(), anyConsumerId(), anyBranchId(), Instant.now(), Version.zero())
    }

    static PlacedOrder placedOrder(OrderId orderId, SalesBranchId salesBranchId) {
        return new PlacedOrder(orderId, anyConsumerId(), salesBranchId, Instant.now(), Version.zero())
    }

    static anyOrderId() {
        return new OrderId(UUID.randomUUID())
    }
}
