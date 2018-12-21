CREATE TABLE category (
id BIGINT AUTO_INCREMENT,
name VARCHAR(50) NOT NULL UNIQUE,
position_number BIGINT,
CONSTRAINT PK_category PRIMARY KEY(id)
);

INSERT INTO category
(name, position_number)
VALUES
("Egyéb", 1),
("Ismeretterjesztő", 2),
("Regény", 3),
("Ezotéria", 4),
("Felnőtt", 5);
