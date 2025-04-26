package com.example.integration.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    void testPersonUpdateFlow() {
        // Create and save a person
        Person person = new Person("Jane Smith", 25);
        personGateway.save(person);

        // Verify initial state
        Person initialPerson = personGateway.findByName("Jane Smith");
        assertNotNull(initialPerson);
        assertEquals(25, initialPerson.getAge());

        // Update the person's age
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("name", "Jane Smith");
        updateParams.put("newAge", 35);
        int updatedCount = personGateway.updatePersonAge(updateParams);

        // Verify update count is 1 (one record updated)
        assertEquals(1, updatedCount);

        // Retrieve the updated person
        Person updatedPerson = personGateway.findByName("Jane Smith");

        // Assert the age was updated
        assertNotNull(updatedPerson);
        assertEquals("Jane Smith", updatedPerson.getName());
        assertEquals(35, updatedPerson.getAge());
    }
}