package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.OrderCancellingReason;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import lombok.NonNull;
import lombok.Value;

@Value
public class CancelOrderCommand {
    @NonNull ConsumerId consumerId;
    @NonNull OrderId orderId;
    @NonNull OrderCancellingReason reason;
}
