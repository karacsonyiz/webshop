CREATE TABLE users (
 id bigint auto_increment,
 user_name VARCHAR(255) NOT NULL UNIQUE,
 full_name VARCHAR(255) NOT NULL,
 password VARCHAR(255) NOT NULL,
 enabled int NOT NULL,
 role VARCHAR(50) NOT NULL,
 CONSTRAINT pk_users PRIMARY KEY(id)
 );

 INSERT INTO users
(user_name, full_name, password, enabled, role)
VALUES("testuser", "Test User", "$2a$10$qQxb4Kd6JxefHd0ThHcMz.bmv2ndCBd0yJaHJe4cs3XDLbYm3fSRW", 1, "ROLE_USER"),
("testadmin", "Test Admin", "$2a$10$nZGfEnGSgOAb5GDhNDngreSmN3BEC2VUzWnLNwV9GvPUXbNsOukBW", 1, "ROLE_ADMIN"),
("pooruser", "Poor User", "$2a$10$nZGfEnGSgOAb5GDhNDngreSmN3BEC2VUzWnLNwV9GvPUXbNsOukBW", 1, "ROLE_USER");