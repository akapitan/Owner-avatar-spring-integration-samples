package com.example.integration.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JpaIntegrationTest {

    @Autowired
    private IntegrationDSLConfig.PersonGateway personGateway;

    @Test
    void testPersonIntegrationFlow() {
        // Create and save a person
        Person person = new Person("John Doe", 30);
        personGateway.save(person);

        // Retrieve the person
        Person foundPerson = personGateway.findByName("John Doe");

        // Assert the results
        assertNotNull(foundPerson);
        assertEquals("John Doe", foundPerson.getName());
        assertEquals(30, foundPerson.getAge());
    }
}