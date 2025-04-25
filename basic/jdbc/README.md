# Spring Integration - JDBC Sample (Java DSL)

## Overview

This sample demonstrates how to use the JDBC components in Spring Integration with the Java DSL. It covers the following
use cases:

- Finding a Person from the database based on the name provided
- Creating a new Person record in the database

This example shows how to use Spring Integration's Java DSL to configure JDBC integration flows. It demonstrates how to
create a messaging gateway interface to interact with the database through Spring Integration.

## Details

The sample uses a simple command-line interface to interact with a H2 in-memory database. It showcases two main flows:

### Find Person Flow

The `findPersonFlow` demonstrates how to use the Integration DSL to query the database for Person records matching a
name pattern. It:

1. Receives a request from the `findPersonChannel`
2. Uses a JdbcTemplate to query the database with a parameterized query
3. Returns a list of matching Person objects

### Create Person Flow

The `createPersonFlow` demonstrates how to:

1. Receive a request from the `createPersonChannel`
2. Insert a new Person record into the database
3. Capture the generated ID from the insert operation
4. Return the created Person with the generated ID

## Running the Sample

You can run the application by either:

- Running the `com.example.basic.jdbc.JdbcApplication` class from your IDE
- Using the command line: `./gradlew :basic:jdbc:bootRun`

When you run the application, you'll see a menu that allows you to:

1. Find a Person by name
2. Create a new Person
3. Exit the application

## Implementation Notes

This sample uses:

- Spring Boot to bootstrap the application
- Spring Integration Java DSL to define integration flows
- JdbcTemplate for database operations
- H2 in-memory database for storage
- Command Line Runner for the user interface

The sample demonstrates the use of `@MessagingGateway` and `@Gateway` annotations to create clean interfaces for
interacting with integration flows.

## Key Files

- `Person.java`: The domain model
- `PersonGateway.java`: The messaging gateway interface
- `JdbcIntegrationConfig.java`: The integration flow configuration
- `JdbcApplication.java`: The main Spring Boot application
- `schema.sql`: Database initialization script
- `Main.java`: Command-line interface