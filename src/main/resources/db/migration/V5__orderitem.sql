CREATE TABLE orderitem (
id BIGINT AUTO_INCREMENT,
order_id BIGINT,
product_id BIGINT,
product_price BIGINT NOT NULL,
CONSTRAINT PK_OrderItem PRIMARY KEY(id),
FOREIGN KEY(order_id) REFERENCES orders(id),
FOREIGN KEY(product_id) REFERENCES products(id)
);