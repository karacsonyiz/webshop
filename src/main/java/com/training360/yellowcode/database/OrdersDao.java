package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrdersDao {

    private JdbcTemplate jdbcTemplate;

    public OrdersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Orders> listOrders() {
        return jdbcTemplate.query("SELECT orders.id, orders.user_id, orders.date, orders.status, " +
                        "orders.delivery_address, COUNT(orderitem.id) AS quantity, " +
                        "SUM(orderitem.product_price) AS price FROM orders " +
                        "LEFT JOIN orderitem ON orders.id = orderitem.order_id " +
                        "GROUP BY orders.id " +
                        "ORDER BY orders.date DESC",
                (ResultSet resultSet, int i) -> new Orders(
                        resultSet.getLong("orders.id"),
                        resultSet.getLong("orders.user_id"),
                        resultSet.getTimestamp("orders.date").toLocalDateTime(),
                        OrderStatus.valueOf(resultSet.getString("orders.status")),
                        resultSet.getLong("quantity"),
                        resultSet.getLong("price"),
                        resultSet.getString("orders.delivery_address")
                ));
    }

    public List<Orders> listActiveOrders() {
        return jdbcTemplate.query("SELECT orders.id, orders.user_id, orders.date, orders.status, " +
                        "orders.delivery_address, COUNT(orderitem.id) AS quantity, " +
                        "SUM(orderitem.product_price) AS price FROM orders " +
                        "LEFT JOIN orderitem ON orders.id = orderitem.order_id " +
                        "WHERE orders.status = 'ACTIVE' " +
                        "GROUP BY orders.id " +
                        "ORDER BY orders.date DESC",
                (ResultSet resultSet, int i) -> new Orders(
                        resultSet.getLong("orders.id"),
                        resultSet.getLong("orders.user_id"),
                        resultSet.getTimestamp("orders.date").toLocalDateTime(),
                        OrderStatus.valueOf(resultSet.getString("orders.status")),
                        resultSet.getLong("quantity"),
                        resultSet.getLong("price"),
                        resultSet.getString("orders.delivery_address")
                ));
    }

    public List<Orders> listOrdersByUserId(long userId) {
        return jdbcTemplate.query("SELECT id, user_id, date, status, delivery_address" +
                        " FROM orders where user_id = ? " +
                        "ORDER BY date DESC",
                new OrderMapper(),
                userId);
    }

    public List<OrderItem> listOrderItems(long userId, long orderId) {
        return jdbcTemplate.query(
                "select orderitem.id, orderitem.order_id, orderitem.product_id, orderitem.product_price, " +
                        "orderitem.quantity, products.name, products.producer, products.address from orderitem " +
                        "join orders on orderitem.order_id = orders.id " +
                        "join products on orderitem.product_id = products.id " +
                        "where orders.id = ? and orders.user_id = ?",
                (ResultSet resultSet, int i) -> new OrderItem(
                        resultSet.getLong("orderitem.id"),
                        resultSet.getLong("orderitem.order_id"),
                        resultSet.getLong("orderitem.product_id"),
                        resultSet.getString("products.name"),
                        resultSet.getString("products.address"),
                        resultSet.getString("products.producer"),
                        resultSet.getLong("orderitem.product_price"),
                        resultSet.getLong("orderitem.quantity")),
                orderId, userId);
    }

    public List<OrderItem> listOrderItemsForAdmin(long orderId) {
        return jdbcTemplate.query(
                "select orderitem.id, orderitem.order_id, orderitem.product_id, orderitem.product_price, " +
                        "orderitem.quantity, products.name, products.producer, products.address from orderitem " +
                        "join orders on orderitem.order_id = orders.id " +
                        "join products on orderitem.product_id = products.id " +
                        "where orders.id = ?",
                (ResultSet resultSet, int i) -> new OrderItem(
                        resultSet.getLong("orderitem.id"),
                        resultSet.getLong("orderitem.order_id"),
                        resultSet.getLong("orderitem.product_id"),
                        resultSet.getString("products.name"),
                        resultSet.getString("products.address"),
                        resultSet.getString("products.producer"),
                        resultSet.getLong("orderitem.product_price"),
                        resultSet.getLong("orderitem.quantity")),
                orderId);
    }


    public void createOrderAndOrderItems(long userId, String address) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into orders(user_id, date, status, delivery_address) values(?, ?, 'ACTIVE', ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, address);
            return ps;
        }, keyHolder);
        long orderId = keyHolder.getKey().longValue();

        jdbcTemplate.update("insert into orderitem (order_id, product_id, product_price, quantity) " +
                "select ?, products.id, products.price, basket.quantity from products " +
                "inner join basket on products.id = basket.product_id " +
                "where basket.user_id = ?", orderId, userId);

        jdbcTemplate.update("delete from basket where user_id = ?", userId);
    }

    public void deleteOrder(long orderId) {
        jdbcTemplate.update("update orders set status = 'DELETED' where id = ? and status = 'ACTIVE'", orderId);
    }

    public void deleteOrderItem(long orderId, String productAddress) {
        jdbcTemplate.update("delete from orderitem where id in (" +
                "select orderitems.id from (select * from orderitem) as orderitems " +
                "join orders on orderitems.order_id = orders.id " +
                "join products on orderitems.product_id = products.id " +
                "where orders.id = ? and products.address = ?)", orderId, productAddress);
    }

    public void modifyActiveStatusToDelivered(long orderId) {
        jdbcTemplate.update("update orders set status = 'DELIVERED' where id = ? and status = 'ACTIVE'", orderId);
    }

    public List<String> listDeliveryAddressesOfUser(long userId) {
        return filterDuplicatesAndNulls(
                jdbcTemplate.queryForList("SELECT delivery_address FROM orders WHERE user_id = ?",
                        String.class,
                        userId)
        );
    }

    private List<String> filterDuplicatesAndNulls(List<String> addresses) {
        List<String> filteredAddresses = new ArrayList<>();
        boolean found = false;
        for (String address : addresses) {
            if (address.length() != 0) {
                for (String filteredAddress : filteredAddresses) {
                    if (address.toLowerCase().equals(filteredAddress.toLowerCase())) {
                        found = true;
                    }
                }
                if (!found) {
                    filteredAddresses.add(address);
                }
                found = false;
            }
        }
        return filteredAddresses;
    }

    private static class OrderMapper implements RowMapper<Orders> {
        @Override
        public Orders mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            long userId = resultSet.getLong("user_id");
            LocalDateTime localDateTime = resultSet.getTimestamp("date").toLocalDateTime();
            OrderStatus status = OrderStatus.valueOf(resultSet.getString("status"));
            String deliveryAddress = resultSet.getString("delivery_address");
            return new Orders(id, userId, localDateTime, status, deliveryAddress);
        }
    }
}
