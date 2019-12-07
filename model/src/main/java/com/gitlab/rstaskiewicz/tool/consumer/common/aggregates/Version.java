package com.gitlab.rstaskiewicz.tool.consumer.common.aggregates;

import lombok.Value;

@Value
public class Version {

    int version;

    public static Version zero() {
        return new Version(0);
    }
}
