ALTER TABLE products
ADD COLUMN category_id BIGINT DEFAULT 1,
ADD FOREIGN KEY (category_id) REFERENCES category(id);

INSERT INTO products
(name, address, producer, price, status, category_id)
VALUES ('Az aliceblue 50 árnyalata', 'az-aliceblue-50-arnyalata', 'E. L. Doe', 9999, 'ACTIVE', 4),
('Legendás programozók és megfigyelésük', 'legendas-programozok-es-megfigyelesuk','J. K. Doe',  3999, 'ACTIVE', 2),
('Az 50 első Trainer osztály', 'az-50-elso-trainer-osztaly', 'Jack Doe', 5999, 'ACTIVE', 3),
('Hogyan neveld a junior fejlesztődet', 'hogyan-neveld-a-junior-fejlesztodet', 'Jane Doe', 6499, 'ACTIVE', 1),
('A Java ura: A classok szövetsége', 'a-java-ura-a-classok-szovetsege', 'J.R.R. Doe', 2899, 'ACTIVE', 2),
('Junioroskert', 'junioroskert', 'Anton Doe', 5599, 'ACTIVE', 3),
('Nemzeti Java', 'nemzeti-java', 'Sándor Doe', 3799, 'ACTIVE', 2),
('A junior csillagok', 'a-junior-csillagok', 'Géza Doe', 4899, 'ACTIVE', 2),
('Egy kis Stackoverflow', 'egy-kis-stackoverflow', 'Darcey Doe', 3999, 'ACTIVE', 1),
('A hengermalomi bárdok', 'a-hengermalomi-bardok', 'János Doe', 8399, 'ACTIVE', 2),
('80 nap alatt a Java körül', '80-nap-alatt-a-java-korul', 'Jules Doe', 6099, 'ACTIVE', 1),
('Junior a szénakazalban', 'junior-a-szenakazalban', 'Ken Doe', 499, 'ACTIVE', 4);