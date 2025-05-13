package com.example.basic.oddeven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

/**
 * Spring Integration DSL flow configuration for routing numbers to odd and even channels.
 */
@Configuration
public class OddEvenConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OddEvenConfig.class);

    @Bean
    public IntegrationFlow oddEvenFlow() {
        return f -> f.
                <Integer, Boolean>route(this::isEven, mapping -> mapping
                        .subFlowMapping(true, evenFlow -> evenFlow
                                .handle(m -> LOGGER.info("Even: {}", m.getPayload()))
                        )
                        .subFlowMapping(false, oddFlow -> oddFlow
                                .handle(m -> LOGGER.info("Odd: {}", m.getPayload()))
                        )
                );
    }

    /**
     * Predicate to check if an integer is even.
     */
    private boolean isEven(Integer n) {
        return n % 2 == 0;
    }
}
