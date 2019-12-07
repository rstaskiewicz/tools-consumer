package com.gitlab.rstaskiewicz.tool.consumer.order

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerFixture.anyConsumerId
import static com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchFixture.anyBranchId

class OrderFixture {

    static PlacedOrder placedOrder() {
        return new PlacedOrder(anyOrderId(), anyBranchId(), anyConsumerId(), Version.zero())
    }

    static PlacedOrder placedOrder(OrderId orderId, SalesBranchId salesBranchId) {
        return new PlacedOrder(orderId, salesBranchId, anyConsumerId(), Version.zero())
    }

    static anyOrderId() {
        return new OrderId(UUID.randomUUID())
    }

}
