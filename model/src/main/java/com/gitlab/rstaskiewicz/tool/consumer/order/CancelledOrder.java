package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.CancellationReason;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PACKAGE;

@Value
@EqualsAndHashCode(of = "orderId")
@RequiredArgsConstructor(access = PACKAGE)
public class CancelledOrder implements Order {

    @NonNull OrderId orderId;

    @NonNull ConsumerId byConsumer;

    @NonNull SalesBranchId placedAt;

    @NonNull CancellationReason cancelledWhy;

    @NonNull Version version;
}
