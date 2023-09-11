DROP TABLE people IF EXISTS;

/*CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);*/


CREATE TABLE people  (
    person_id BIGINT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    PRIMARY KEY (person_id)
);
