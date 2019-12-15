package com.gitlab.rstaskiewicz.tool.consumer

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Import

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(InfrastructureConfiguration.class)
class InfrastructureTestConfiguration {
}
