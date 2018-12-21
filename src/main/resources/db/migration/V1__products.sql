CREATE TABLE products (
 id bigint auto_increment,
 name VARCHAR(255) NOT NULL,
 address VARCHAR(255) NOT NULL UNIQUE,
 producer VARCHAR(255) NOT NULL,
 price bigint NOT NULL,
 status VARCHAR(50) NOT NULL,
 CONSTRAINT pk_products PRIMARY KEY(id)
 );


INSERT INTO products
(name, address, producer, price, status)
VALUES
('Junior most és mindörökké', 'junior-most-es-mindorokke', 'James Doe', 2999, 'ACTIVE'),
('Junior fejlesztő falinaptár 2019', 'junior-fejleszto-falinaptar-2019', 'Peter Doe', 4699, 'ACTIVE'),
('Juniorsoron', 'juniorsoron', 'Stephen Doe', 2999, 'ACTIVE');