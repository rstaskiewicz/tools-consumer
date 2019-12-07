package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.PaymentDeadline;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import lombok.NonNull;
import lombok.Value;

import static com.gitlab.rstaskiewicz.tool.consumer.consumer.NumberOfDays.of;

@Value
public class PlaceOrderCommand {

    @NonNull ConsumerId consumerId;
    @NonNull SalesBranchId salesBranchId;
    @NonNull OrderId orderId;
    int daysToDeadline;

    PaymentDeadline getPaymentDeadline() {
        return PaymentDeadline.forNumberOfDays(of(daysToDeadline));
    }
}
