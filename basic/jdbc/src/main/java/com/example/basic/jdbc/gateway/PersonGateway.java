package com.example.basic.jdbc.gateway;

import com.example.basic.jdbc.domain.Person;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.List;

/**
 * Gateway interface for Person database operations.
 */
@MessagingGateway
public interface PersonGateway {

    /**
     * Find a person by name.
     *
     * @param name the first name or part of it
     * @return list of matching persons
     */
    @Gateway(requestChannel = "findPersonChannel")
    List<Person> findByName(String name);

    /**
     * Create a new person.
     *
     * @param person the person to create
     * @return the created person with populated ID
     */
    @Gateway(requestChannel = "createPersonChannel")
    Person createPerson(Person person);
}