package com.example.basic.jdbc;

import com.example.basic.jdbc.domain.Person;
import com.example.basic.jdbc.gateway.PersonGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JdbcIntegrationTest {

    @Autowired
    private PersonGateway personGateway;

    @Test
    void testFindPersonByName() {
        List<Person> persons = personGateway.findByName("John");

        assertNotNull(persons);
        assertEquals(1, persons.size());
        assertEquals("John", persons.getFirst().getFirstName());
        assertEquals("Doe", persons.getFirst().getLastName());
        assertEquals(42, persons.getFirst().getAge());
    }

    @Test
    void testCreatePerson() {
        Person personToCreate = new Person("Test", "User", 30);

        Person createdPerson = personGateway.createPerson(personToCreate);

        assertNotNull(createdPerson);
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Test", createdPerson.getFirstName());
        assertEquals("User", createdPerson.getLastName());
        assertEquals(30, createdPerson.getAge());

        // Verify the person can be found after creation
        List<Person> foundPersons = personGateway.findByName("Test");
        assertEquals(1, foundPersons.size());
        assertEquals(createdPerson.getId(), foundPersons.getFirst().getId());
    }

    @Test
    void testFindByPartialName() {
        // Should find both John and Jane
        List<Person> persons = personGateway.findByName("J");

        assertNotNull(persons);
        assertEquals(3, persons.size());

        // Check that we got the expected names
        boolean foundJohn = false;
        boolean foundJane = false;
        boolean foundJames = false;

        for (Person person : persons) {
            if ("John".equals(person.getFirstName())) {
                foundJohn = true;
            } else if ("Jane".equals(person.getFirstName())) {
                foundJane = true;
            } else if ("James".equals(person.getFirstName())) {
                foundJames = true;
            }
        }

        assertTrue(foundJohn);
        assertTrue(foundJane);
        assertTrue(foundJames);
    }
}