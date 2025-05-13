package com.basic.example.websocket.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.outbound.WebSocketOutboundMessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * WebSocket Server Application using Spring Integration Java config
 */
@SpringBootApplication
public class WebSocketServerApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(WebSocketServerApplication.class, args);
        System.out.println("WebSocket Server is running. Hit 'Enter' to terminate.");
        System.in.read();
        ctx.close();
    }

    @Bean
    public ServerWebSocketContainer serverWebSocketContainer() {
        return new ServerWebSocketContainer("/time").withSockJs();
    }

/*    @Bean
    @InboundChannelAdapter(value = "splitChannel", poller = @Poller(fixedDelay = "1000", maxMessagesPerPoll = "1"))
    public MessageSource<?> webSocketSessionsMessageSource() {
        return () -> new GenericMessage<>(serverWebSocketContainer().getSessions().keySet().iterator());
    }

    @Bean
    public MessageChannel splitChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "splitChannel")
    public MessageHandler splitter() {
        DefaultMessageSplitter splitter = new DefaultMessageSplitter();
        splitter.setOutputChannelName("headerEnricherChannel");
        return splitter;
    }

    @Bean
    public MessageChannel headerEnricherChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    @Bean
    @Transformer(inputChannel = "headerEnricherChannel", outputChannel = "transformChannel")
    public HeaderEnricher headerEnricher() {
        return new HeaderEnricher(Collections.singletonMap(SimpMessageHeaderAccessor.SESSION_ID_HEADER,
                new ExpressionEvaluatingHeaderValueMessageProcessor<>("payload", String.class)));
    }

    @Bean
    public MessageChannel transformChannel() {
        return new DirectChannel();
    }

    @Bean
    @Transformer(inputChannel = "transformChannel", outputChannel = "sendTimeChannel")
    public AbstractPayloadTransformer<?, ?> transformer() {
        return new AbstractPayloadTransformer<Object, Object>() {
            @Override
            protected Object transformPayload(Object payload) {
                System.out.println(payload);
                return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.DEFAULT).format(new Date());
            }
        };
    }

    @Bean
    public MessageChannel sendTimeChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "sendTimeChannel")
    public MessageHandler webSocketOutboundAdapter() {
        return new WebSocketOutboundMessageHandler(serverWebSocketContainer());
    }

    @Bean
    @ServiceActivator(inputChannel = "sendTimeChannel")
    public MessageHandler loggingChannelAdapter() {
        LoggingHandler loggingHandler = new LoggingHandler(LoggingHandler.Level.INFO);
        loggingHandler.setLoggerName("wsLog");
        loggingHandler.setLogExpressionString(
                "'The time ' + payload + ' has been sent to the WebSocketSession ' + headers.simpSessionId");
        return loggingHandler;
    }*/

    @Bean
    public IntegrationFlow webSocketFlow() {
        return IntegrationFlow.from(() -> new GenericMessage<>(serverWebSocketContainer().getSessions().keySet().toArray()),
                        e -> e.poller(Pollers.fixedDelay(1000).maxMessagesPerPoll(1)))
                .split()
//                .log(LoggingHandler.Level.INFO, "wsLog-headers", "headers")
                .channel(c -> c.executor(Executors.newVirtualThreadPerTaskExecutor()))
                .enrichHeaders(h -> h.headerFunction(SimpMessageHeaderAccessor.SESSION_ID_HEADER,
                        m -> m.getPayload().toString()))
                .transform((GenericTransformer<Object, Object>) _ ->
                        DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.DEFAULT).format(new Date()))
                .publishSubscribeChannel(pubsub -> pubsub
                        .subscribe(subflow -> subflow
                                .handle(new WebSocketOutboundMessageHandler(serverWebSocketContainer())))
//                        .subscribe(subflow -> subflow
//                                .log(LoggingHandler.Level.INFO, "wsLog",
//                                        "'The time ' + payload + ' has been sent to the WebSocketSession ' + headers.simpSessionId").nullChannel())
                        .subscribe(subflow -> subflow
                                .aggregate(/*aggregator -> aggregator
                                        .correlationStrategy(new HeaderAttributeCorrelationStrategy("correlationId"))
                                        .releaseStrategy(new SimpleSequenceSizeReleaseStrategy())
                                        .expireGroupsUponCompletion(true)
                                        .sendPartialResultOnExpiry(true)
                                        .expireGroupsUponTimeout(true)*/)
                                .log(LoggingHandler.Level.INFO, "wsLog-aggregated", m -> "Aggregated size: " + ((Collection<?>) m.getPayload()).size())
                                .nullChannel()))
                .get();
    }
}