package com.gitlab.rstaskiewicz.tool.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.common.events.ConsumerEventSource;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan
@EnableScheduling
@EnableBinding({ConsumerEventSource.class})
class InfrastructureConfiguration { }
