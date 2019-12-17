package com.gitlab.rstaskiewicz.tool.consumer.common.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.ConsumerCreated;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.MaximumNumberOfDailyOrdersReached;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCancelingFailed;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlaced;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacingFailed;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = ConsumerCreated.TYPE, value = ConsumerCreated.class),
        @JsonSubTypes.Type(name = OrderPlaced.TYPE, value = OrderPlaced.class),
        @JsonSubTypes.Type(name = MaximumNumberOfDailyOrdersReached.TYPE, value = MaximumNumberOfDailyOrdersReached.class),
        @JsonSubTypes.Type(name = OrderPlacingFailed.TYPE, value = OrderPlacingFailed.class),
        @JsonSubTypes.Type(name = OrderCanceled.TYPE, value = OrderCanceled.class),
        @JsonSubTypes.Type(name = OrderCancelingFailed.TYPE, value = OrderCancelingFailed.class),
})
public interface DomainEvent {

    UUID getEventId();

    Instant getWhen();

    UUID getAggregateId();

    String getType();
}
