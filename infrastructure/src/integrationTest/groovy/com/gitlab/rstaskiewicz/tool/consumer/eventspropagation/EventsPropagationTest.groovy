package com.gitlab.rstaskiewicz.tool.consumer.eventspropagation


import com.fasterxml.jackson.databind.ObjectMapper
import com.gitlab.rstaskiewicz.tool.consumer.IntegrationTest
import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvent
import com.gitlab.rstaskiewicz.tool.consumer.common.events.ConsumerEventSource
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.Message

import java.util.concurrent.BlockingQueue

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat

class EventsPropagationTest extends IntegrationTest {

    @Autowired MessageCollector messageCollector
    @Autowired ConsumerEventSource source

    BlockingQueue<Message<?>> events

    ObjectMapper objectMapper = new ObjectMapper()

    def setup() {
        events = messageCollector.forChannel(source.output())
    }

    def 'should propagate order placed event'() {
        given:
            consumerPersistedInDatabase()
        and:
            ConsumerEvent.OrderPlaced orderPlacedEvent = orderPlaced()
        when:
            consumers.publish(orderPlacedEvent)
        then:
            assertThat(events, receivesPayloadThat(is(serialized(orderPlacedEvent))))
    }

    def 'should propagate order cancelled event'() {
        given:
            consumerPersistedInDatabase()
        and:
            ConsumerEvent.OrderCanceled orderCanceledEvent = orderCanceled()
        when:
            consumers.publish(orderCanceledEvent)
        then:
            assertThat(events, receivesPayloadThat(is(serialized(orderCanceledEvent))))
    }

    String serialized(DomainEvent event) {
        return objectMapper.writeValueAsString(event)
    }
}
