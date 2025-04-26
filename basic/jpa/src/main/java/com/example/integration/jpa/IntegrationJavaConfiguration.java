package com.example.integration.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableTransactionManagement
public class IntegrationJavaConfiguration {

    @Bean
    public JpaExecutor jpaExecutor(EntityManagerFactory entityManagerFactory) {
        JpaExecutor executor = new JpaExecutor(entityManagerFactory);
        executor.setJpaQuery("FROM Person");
        return executor;
    }

    @Bean
    @InboundChannelAdapter(channel = "personPollingChannel",
            poller = @Poller(fixedDelay = "5000"))
    public JpaPollingChannelAdapter jpaPollingChannelAdapter(EntityManagerFactory entityManagerFactory) {
        return new JpaPollingChannelAdapter(jpaExecutor(entityManagerFactory));
    }

    @Bean
    @ServiceActivator(inputChannel = "personPollingChannel")
    public MessageHandler personPollingHandler() {
        return message -> {
            if (message.getPayload() instanceof List<?> persons) {
                persons.forEach(person -> System.out.println("Polled person: " + person));
            } else {
                System.out.println("Polled person: " + message.getPayload());
            }
        };
    }
}
