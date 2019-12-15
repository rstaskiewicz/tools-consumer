package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
class ConsumerOrderEntity {

    @Id
    Long id;
    UUID orderId;
    UUID consumerId;
    UUID salesBranchId;
    Instant orderedAt;
    Instant paymentTill;

    ConsumerOrderEntity(UUID orderId, UUID consumerId, UUID salesBranchId, Instant orderedAt, Instant paymentTill) {
        this.orderId = orderId;
        this.consumerId = consumerId;
        this.salesBranchId = salesBranchId;
        this.orderedAt = orderedAt;
        this.paymentTill = paymentTill;
    }

    boolean is(UUID consumerId, UUID orderId, UUID salesBranchId) {
        return this.orderId.equals(orderId)
                && this.consumerId.equals(consumerId)
                && this.salesBranchId.equals(salesBranchId);
    }
}
