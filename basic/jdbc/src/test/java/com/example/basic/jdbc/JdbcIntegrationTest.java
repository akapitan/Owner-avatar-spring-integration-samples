package com.example.basic.jdbc;

import com.example.basic.jdbc.domain.Person;
import com.example.basic.jdbc.gateway.PersonGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcIntegrationTest {

    @Autowired
    private PersonGateway personGateway;

    @Test
    public void testFindPersonByName() {
        List<Person> persons = personGateway.findByName("John");

        assertNotNull(persons);
        assertEquals(1, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
        assertEquals("Doe", persons.get(0).getLastName());
        assertEquals(42, persons.get(0).getAge());
    }

    @Test
    public void testCreatePerson() {
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
        assertEquals(createdPerson.getId(), foundPersons.get(0).getId());
    }

    @Test
    public void testFindByPartialName() {
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