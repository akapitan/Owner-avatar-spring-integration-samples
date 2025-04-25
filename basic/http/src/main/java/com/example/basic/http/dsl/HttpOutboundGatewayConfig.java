package com.example.basic.http.dsl;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;

//@Configuration
public class HttpOutboundGatewayConfig {

    @Bean
    public IntegrationFlow outboundHttpFlow() {
        return IntegrationFlow.from("requestChannel")
                .handle(Http.outboundGateway("http://localhost:8080/greeting")
                        .httpMethod(HttpMethod.POST)
                        .expectedResponseType(String.class))
                .channel("responseChannel")
                .get();
    }
}