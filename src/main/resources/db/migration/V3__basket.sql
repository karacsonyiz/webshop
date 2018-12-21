CREATE TABLE basket(
id BIGINT auto_increment,
user_id BIGINT,
product_id bigint,
CONSTRAINT pk_basket PRIMARY KEY(id),
FOREIGN KEY (product_id) REFERENCES products(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);
