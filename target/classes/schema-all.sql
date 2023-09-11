DROP TABLE people IF EXISTS;
-- Table to use in hsql
/*CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);*/

-- Table for h2
CREATE TABLE people  (
    person_id BIGINT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    PRIMARY KEY (person_id)
);

--MERGE INTO people 
-- (first_name, last_name)
--  KEY(person_id) 
--VALUES ('JILL', 'GREEN'),
--  (JOE, 'GREEN'),
--  (JUSTIN, 'GREEN'),
--  (JANE, 'GREEN'),
--  (JOHN, 'GREEN'); 

  

