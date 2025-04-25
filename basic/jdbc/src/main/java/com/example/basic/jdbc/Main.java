package com.example.basic.jdbc;

import com.example.basic.jdbc.domain.Person;
import com.example.basic.jdbc.gateway.PersonGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Scanner;

@Configuration
@Profile("!test")
public class Main {

    @Bean
    public CommandLineRunner run(PersonGateway personGateway) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("\n==== Spring Integration JDBC Sample ====");
                System.out.println("\n1. Find a Person by name");
                System.out.println("2. Create a new Person");
                System.out.println("3. Exit");
                System.out.print("\nSelect an option: ");

                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1:
                        findPersonByName(personGateway, scanner);
                        break;
                    case 2:
                        createPerson(personGateway, scanner);
                        break;
                    case 3:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        };
    }

    private static void findPersonByName(PersonGateway personGateway, Scanner scanner) {
        System.out.print("Enter name to search for: ");
        String name = scanner.nextLine();

        List<Person> persons = personGateway.findByName(name);

        System.out.println("\nSearch Results:");
        if (persons.isEmpty()) {
            System.out.println("No persons found with name containing: " + name);
        } else {
            for (Person person : persons) {
                System.out.println(person);
            }
        }
    }

    private static void createPerson(PersonGateway personGateway, Scanner scanner) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Person personToCreate = new Person(firstName, lastName, age);
        Person createdPerson = personGateway.createPerson(personToCreate);

        System.out.println("\nCreated Person:");
        System.out.println(createdPerson);
    }
}