package com.example.integration.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.integration.jpa.outbound.JpaOutboundGateway;
import org.springframework.integration.jpa.support.PersistMode;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@EnableTransactionManagement
public class IntegrationJavaConfiguration {

    //    JPA Inbound Adapter - Java Configuration
    @Bean
    public JpaExecutor jpaInboundExecutor(EntityManagerFactory entityManagerFactory) {
        JpaExecutor executor = new JpaExecutor(entityManagerFactory);
        executor.setJpaQuery("FROM Person");
        return executor;
    }

    @Bean
    @InboundChannelAdapter(channel = "personPollingChannel",
            poller = @Poller(fixedDelay = "5000"))
    public JpaPollingChannelAdapter jpaPollingChannelAdapter(EntityManagerFactory entityManagerFactory) {
        return new JpaPollingChannelAdapter(jpaInboundExecutor(entityManagerFactory));
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

    // JPA Outbound Adapter

    @Autowired
    EntityManager entityManager;

    @MessagingGateway
    interface JpaGateway {

        @Gateway(requestChannel = "jpaPersistChannel")
        @Transactional
        void persistStudent(Person payload);

    }

    @Bean
    public JpaExecutor jpaOutboundExecutor() {
        JpaExecutor executor = new JpaExecutor(this.entityManager);
        executor.setEntityClass(Person.class);
        executor.setPersistMode(PersistMode.PERSIST);
        return executor;
    }

    @Bean
    @ServiceActivator(inputChannel = "jpaPersistChannel")
    public MessageHandler jpaOutbound() {
        JpaOutboundGateway adapter = new JpaOutboundGateway(jpaOutboundExecutor());
        adapter.setProducesReply(false);
        return adapter;
    }
}
