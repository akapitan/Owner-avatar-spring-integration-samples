package com.example.integration.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testPersonRetrieveByAgeFlow() {
        // Create and save several persons with different ages using unique names for this test
        personGateway.save(new Person("Retrieve Test John", 30));
        personGateway.save(new Person("Retrieve Test Jane", 25));
        personGateway.save(new Person("Retrieve Test Bob", 40));
        personGateway.save(new Person("Retrieve Test Alice", 35));
        personGateway.save(new Person("Retrieve Test Charlie", 22));

        // Retrieve persons with minimum age of 35
        List<Person> results = personGateway.findByMinimumAge(35);

        // Verify the results
        assertNotNull(results);
        assertEquals(2, results.size());

        // Verify all returned persons have age >= 35
        for (Person person : results) {
            assertTrue(person.getAge() >= 35);
        }

        // Verify that correct persons are returned
        List<String> names = results.stream().map(Person::getName).sorted().toList();
        assertEquals("Retrieve Test Alice", names.get(0));
        assertEquals("Retrieve Test Bob", names.get(1));
    }
}