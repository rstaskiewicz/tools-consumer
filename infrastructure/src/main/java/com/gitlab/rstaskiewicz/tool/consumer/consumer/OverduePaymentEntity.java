package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
public class OverduePaymentEntity {

    @Id
    Long id;
    UUID consumerId;
    UUID orderId;
    UUID salesBranchId;

    OverduePaymentEntity(UUID consumerId, UUID orderId, UUID salesBranchId) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.salesBranchId = salesBranchId;
    }

    boolean is(UUID consumerId, UUID orderId, UUID salesBranchId) {
        return this.consumerId.equals(consumerId)
                && this.orderId.equals(orderId)
                && this.salesBranchId.equals(salesBranchId);
    }
}
