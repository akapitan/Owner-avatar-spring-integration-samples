package com.example.basic.http.dsl;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;

//@Configuration
public class HttpInboundGatewayConfig {

    @Bean
    public IntegrationFlow httpInboundFlow() {
        return IntegrationFlow.from(Http.inboundGateway("/greeting")
                        .requestMapping(m -> m.methods(HttpMethod.POST))
                        .statusCodeExpression("T(org.springframework.http.HttpStatus).OK"))
                .handle((payload, headers) -> {
                    String name;
                    if (payload instanceof byte[]) {
                        name = new String((byte[]) payload);
                    } else {
                        name = payload.toString();
                    }
                    System.out.println("Received request with name: " + name);
                    return "Hello, " + name;
                })
                .get();
    }
}