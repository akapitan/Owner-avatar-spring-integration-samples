package com.example.basic.helloworld;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class HelloService {

    @ServiceActivator(inputChannel = "inputChannel", outputChannel = "outputChannel")
    public String sayHello(String name) {
        return "Hello " + name;
    }

}