package com.example.integration.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.ConsumerEndpointSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.PollerFactory;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class IntegrationDSLConfig {

    @MessagingGateway
    public interface PersonGateway {
        @Gateway(requestChannel = "personInput")
        @Transactional
        void save(Person person);

        @Gateway(requestChannel = "personRetrieveInput")
        Person findByName(String name);

        @Gateway(requestChannel = "personUpdateInput")
        int updatePersonAge(Map<String, Object> params);

        @Gateway(requestChannel = "personRetrieveByAgeInput")
        List<Person> findByMinimumAge(int age);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Transactional
    public IntegrationFlow personSaveFlow(EntityManager entityManager) {
        return IntegrationFlow.from("personInput")
//                .<Person>handle((person, headers) -> {
//                    entityManager.persist(person);
//                    return person;
//                })
                .handle(Jpa.outboundAdapter(entityManager)
                        .entityClass(Person.class)
                        .persistMode(PersistMode.PERSIST), ConsumerEndpointSpec::transactional)
                .get();
    }

    @Bean
    @Transactional
    public IntegrationFlow personRetrieveFlow(EntityManager entityManager) {
        return IntegrationFlow.from("personRetrieveInput")
                .<String>handle((payload, headers) -> {
                    TypedQuery<Person> query = entityManager.createQuery(
                            "FROM Person p WHERE p.name = :name", Person.class);
                    query.setParameter("name", payload);
                    List<Person> results = query.getResultList();
                    return results.isEmpty() ? null : results.getFirst();
                })
                .get();
    }

    @Bean
    public IntegrationFlow pollingAdapterFlow(EntityManagerFactory entityManagerFactory) {
        return IntegrationFlow
                .from(Jpa.inboundAdapter(entityManagerFactory)
                                .entityClass(Person.class)
                                .maxResults(1)
                                .expectSingleResult(true)
                                .deleteAfterPoll(true),
                        e -> e.poller(p -> PollerFactory.fixedDelay(Duration.ofSeconds(5))))
                .channel(c -> c.queue("pollingResults"))
                .log().nullChannel();
    }

    //    JPA Outbound Gateway - updating
    @Bean
    @Transactional
    public IntegrationFlow personUpdateFlow(EntityManagerFactory entityManagerFactory) {
        return IntegrationFlow.from("personUpdateInput")
                .handle(Jpa.updatingGateway(entityManagerFactory)
                                .jpaQuery("UPDATE Person p SET p.age = :newAge WHERE p.name = :name")
                                .parameterExpression("name", "payload.name")
                                .parameterExpression("newAge", "payload.newAge"),
                        ConsumerEndpointSpec::transactional)
                .get();
    }

    @Bean
    @Transactional
    public IntegrationFlow personRetrieveByAgeFlow(EntityManagerFactory entityManagerFactory) {
        return IntegrationFlow.from("personRetrieveByAgeInput")
                .handle(Jpa.retrievingGateway(entityManagerFactory)
                                .jpaQuery("FROM Person p WHERE p.age >= :minAge")
                                .parameterExpression("minAge", "payload")
                                .expectSingleResult(false)
                                .deleteAfterPoll(false),
                        ConsumerEndpointSpec::transactional)
                .get();
    }
}