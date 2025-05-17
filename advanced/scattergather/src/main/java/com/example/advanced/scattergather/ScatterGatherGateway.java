package com.example.advanced.scattergather;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface ScatterGatherGateway {

    @Gateway(requestChannel = "scatterGather.input")
    Message<?> exchange(String message);
}
