package com.gitlab.rstaskiewicz.tool.consumer.salesbranch

class SalesBranchFixture {

    static SalesBranchId anyBranchId() {
        return new SalesBranchId(UUID.randomUUID())
    }
}
