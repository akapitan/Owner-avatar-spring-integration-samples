package com.example.advanced.scattergather;

import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.ExecutorChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
class ScatterGatherFlowConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ScatterGatherFlowConfiguration.class);

    @Bean
    ExecutorChannelSpec processingChannel() {
        return MessageChannels.executor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    IntegrationFlow scatterGather() {
        return f -> f
                .scatterGather(
                        scatterer -> scatterer
                                .applySequence(true)
                                .recipientFlow(m -> true, sf -> sf
                                        .channel(processingChannel())
                                        .handle((p, h) -> {
                                            LOG.info("Service 1 processing: {}", p);
                                            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                                            return p + " - processed by Service 1";
                                        }))
                                .recipientFlow(m -> true, sf -> sf
                                        .channel(processingChannel())
                                        .handle((p, h) -> {
                                            LOG.info("Service 2 processing: {}", p);
                                            Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
                                            return p + " - processed by Service 2";
                                        }))
                                .recipientFlow(m -> true, sf -> sf
                                        .channel(processingChannel())
                                        .handle((p, h) -> {
                                            LOG.info("Service 3 processing: {}", p);
                                            Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
                                            return p + " - processed by Service 3";
                                        })),
                        gatherer -> gatherer
                                .outputProcessor(group -> {
                                    StringBuilder result = new StringBuilder();
                                    group.getMessages().forEach(m -> result.append(m.getPayload()));
                                    return result.toString().trim();
                                })
                )
                .handle((payload, headers) -> {
                    LOG.info("Final result: {}", payload);
                    LOG.info("Final headers: {}", headers);
                    return payload;
                });
    }

    @Bean
    IntegrationFlow initiateFlow() {
        return IntegrationFlow.fromSupplier(() -> "Test Message",
                        e -> e.poller(Pollers.fixedDelay(5000)))
                .channel("scatterGather.input")
                .get();
    }
}