package com.example.integration.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Transactional
    public IntegrationFlow personSaveFlow(EntityManager entityManager) {
        return IntegrationFlow.from("personInput")
                .<Person>handle((person, headers) -> {
                    entityManager.persist(person);
                    return person;
                })
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
}