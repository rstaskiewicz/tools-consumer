package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.NonNull;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;

@Value
class PaymentDeadline {

    @NonNull Instant when;

    static PaymentDeadline forNumberOfDays(NumberOfDays days) {
        Instant till = Instant.now().plus(Duration.ofDays(days.getDays()));
        return new PaymentDeadline(till);
    }
}
