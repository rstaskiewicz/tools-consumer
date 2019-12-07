package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import lombok.Value;

@Value
class NumberOfDays {

    int days;

    private NumberOfDays(int days) {
        if(days <= 0) {
            throw new IllegalArgumentException("Cannot use negative integer or zero as number of days");
        }
        this.days = days;
    }

    static NumberOfDays of(int days) {
        return new NumberOfDays(days);
    }
}

