package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.Basket;
import com.training360.yellowcode.dbTables.OrderStatus;
import com.training360.yellowcode.dbTables.Orders;
import com.training360.yellowcode.dbTables.Reports;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class ReportsDao {

    private JdbcTemplate jdbcTemplate;

    public ReportsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reports> listReportsByDate() {
        return jdbcTemplate.query("SELECT SUM(product_price * quantity),date,status,SUM(quantity) " +
                        "FROM orderitem " +
                        "JOIN orders on orderitem.order_id = orders.id " +
                        "WHERE YEAR(date) = YEAR(CURDATE()) " +
                        "GROUP BY month(date),status",
                (ResultSet resultSet, int i) -> new Reports(
                        resultSet.getLong("SUM(product_price * quantity)"),
                        resultSet.getTimestamp("date").toLocalDateTime(),
                        OrderStatus.valueOf(resultSet.getString("status")),
                        resultSet.getLong("SUM(quantity)")
                ));
    }

    public List<Reports> listReportsByProductAndDate(){
    return jdbcTemplate.query(
                    "SELECT products.name,month(orders.date),SUM(quantity)," +
                    "products.price,products.price*SUM(quantity) " +
                    "FROM orderitem JOIN products on orderitem.product_id = products.id " +
                    "JOIN orders on orderitem.order_id = orders.id " +
                    "WHERE orders.status = 'DELIVERED' AND YEAR(date) = YEAR(CURDATE()) " +
                    "GROUP BY month(orders.date), products.id,products.name",
            (ResultSet resultSet, int i) -> new Reports(
                        resultSet.getString("products.name"),
                        resultSet.getInt("month(orders.date)"),
                        resultSet.getLong("SUM(quantity)"),
                        resultSet.getLong("products.price"),
                        resultSet.getLong("products.price*SUM(quantity)")
                ));
    }
}
