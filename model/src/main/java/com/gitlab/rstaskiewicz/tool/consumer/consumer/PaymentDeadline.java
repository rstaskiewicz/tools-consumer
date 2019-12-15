package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.NonNull;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;

@Value
class PaymentDeadline {

    @NonNull Instant till;

    private PaymentDeadline(Instant orderTime, Instant paymentTill) {
        if (paymentTill.isBefore(orderTime)) {
            throw new IllegalStateException("Close-ended duration must be valid");
        }
        this.till = paymentTill;
    }

    public static PaymentDeadline forNumberOfDays(NumberOfDays days, OrderTime orderTime) {
        Instant paymentTill = orderTime.getWhen().plus(Duration.ofDays(days.getDays()));
        return new PaymentDeadline(orderTime.getWhen(), paymentTill);
    }
}
