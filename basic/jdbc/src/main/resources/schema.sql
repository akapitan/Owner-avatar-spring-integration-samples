DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    age        INT         NOT NULL
);

INSERT INTO person (first_name, last_name, age)
VALUES ('John', 'Doe', 42);
INSERT INTO person (first_name, last_name, age)
VALUES ('Jane', 'Smith', 38);
INSERT INTO person (first_name, last_name, age)
VALUES ('James', 'Brown', 25);