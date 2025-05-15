package com.example.advanced.scattergather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration(proxyBeanMethods = false)
class ScatterGatherFlowConfiguration {

/*    @Bean
    IntegrationFlow scatterGather() {
        return f -> f.scatterGather(
                s -> s.recipientFlow().recipient
        );
    }*/

}
