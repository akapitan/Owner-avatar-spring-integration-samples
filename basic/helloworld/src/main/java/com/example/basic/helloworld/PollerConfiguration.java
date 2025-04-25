package com.example.basic.helloworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.PollableChannel;

import java.util.function.Supplier;

@Configuration
@EnableIntegration
public class PollerConfiguration {

    @Bean
    public PollableChannel inputChannel2() {
        return new QueueChannel();
    }

    @Bean
    public PollableChannel outputChannel2() {
        return new QueueChannel();
    }

    @MessagingGateway
    public interface HelloGateway {
        @Gateway(requestChannel = "inputChannel2", replyChannel = "outputChannel2")
        String sendMessage(String name);
    }

    @Bean
    @InboundChannelAdapter(value = "inputChannel2")
    public Supplier<String> messageProvider() {
        return () -> "Spring Integration!";
    }

    @ServiceActivator(inputChannel = "inputChannel2", outputChannel = "outputChannel2")
    public String helloService(String name) {
        return "Hello " + name;
    }

}