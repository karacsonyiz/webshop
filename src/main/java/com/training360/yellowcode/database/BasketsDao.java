package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.Basket;
import com.training360.yellowcode.dbTables.BasketProduct;
import com.training360.yellowcode.dbTables.Product;
import com.training360.yellowcode.dbTables.ProductStatusType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BasketsDao {

    private JdbcTemplate jdbcTemplate;

    public BasketsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Basket> findBasketByUserIdAndProductId(Basket basket) {
        return jdbcTemplate.query("SELECT id, user_id, product_id, quantity" +
                        " FROM basket WHERE user_id = ? and product_id = ?",
                new BasketMapper(),
                basket.getUserId(),
                basket.getProductId());
    }

    public void addToBasket(Basket basket) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into basket(user_id,product_id, quantity)" +
                    " values(?,?,?)");
            ps.setLong(1, basket.getUserId());
            ps.setLong(2, basket.getProductId());
            ps.setLong(3, basket.getQuantity());
            return ps;
        });
    }

    public void increaseBasketQuantityByOne(Basket basket) {
        jdbcTemplate.update("update basket set quantity = ? where id = ?",
                basket.getQuantity() + 1,
                basket.getId());
    }

    public void decreaseBasketQuantityByOne(Basket basket) {
        jdbcTemplate.update("update basket set quantity = ? where id = ?",
                basket.getQuantity() - 1,
                basket.getId());
    }

    public void setBasketQuantity(Basket basket, long quantity) {
        jdbcTemplate.update("update basket set quantity = ? where id = ?",
                quantity,
                basket.getId());
    }

    public List<BasketProduct> listProducts(long userId) {
        return jdbcTemplate.query(
                "SELECT products.id, products.name, products.address, products.producer, products.price, basket.quantity" +
                        " FROM products " +
                        "LEFT JOIN basket on products.id = basket.product_id " +
                        "WHERE basket.user_id = ? AND products.status = 'ACTIVE'",
                (ResultSet resultSet, int i) ->
                        new BasketProduct(resultSet.getLong("products.id"),
                                resultSet.getString("products.name"),
                                resultSet.getString("products.address"),
                                resultSet.getString("products.producer"),
                                resultSet.getLong("products.price"),
                                resultSet.getLong("basket.quantity")),
                userId);
    }

    private static class BasketMapper implements RowMapper<Basket> {
        @Override
        public Basket mapRow(ResultSet resultSet, int i) throws SQLException {
            Long id = resultSet.getLong("id");
            long userId = resultSet.getLong("user_id");
            long productId = resultSet.getLong("product_id");
            long quantity = resultSet.getLong("quantity");
            return new Basket(id, userId, productId, quantity);
        }
    }

    public void deleteFromBasketByUserId(long userId) {
        jdbcTemplate.update("delete from basket where user_id = ?", userId);
    }

    public void deleteFromBasketByProductIdAndUserId(long userId, long productId) {
        jdbcTemplate.update("delete from basket where user_id = ? AND product_id = ?", userId, productId);
    }
}