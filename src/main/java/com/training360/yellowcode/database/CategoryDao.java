package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDao {

    private JdbcTemplate jdbcTemplate;

    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<Category> findCategoryById(long id) {
        try {
            Category category = jdbcTemplate.queryForObject(
                    "SELECT id, name, position_number FROM category WHERE id = ?",
                    new CategoryMapper(), id);
            return Optional.of(category);
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public Optional<Category> findCategoryByName(String name) {
        try {
            Category category = jdbcTemplate.queryForObject(
                    "SELECT id, name, position_number FROM category WHERE name = ?",
                    new CategoryMapper(), name);
            return Optional.of(category);
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public List<Category> listCategorys() {
        return jdbcTemplate.query(
                "select id, name, position_number FROM category ORDER BY position_number",
                new CategoryDao.CategoryMapper());
    }

    private static class CategoryMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            Long positionNumber = resultSet.getLong("position_number");
            return new Category(id, name, positionNumber);
        }
    }

    public void createCategory(Category category) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into category(name, position_number) values(?, ?)"
                );
                ps.setString(1, category.getName());
                ps.setLong(2, category.getPositionNumber());

                return ps;
            }
        });
    }

    public void updateCategory(Category category) {
        String oldCategoryName = findCategoryById(category.getId()).get().getName();

        if (oldCategoryName.equals(category.getName())) {
            jdbcTemplate.update("update category set position_number = ? where id = ?",
                    category.getPositionNumber(),
                    category.getId());
        } else if (!oldCategoryName.equals(category.getName()) && !findCategoryByName(category.getName()).isPresent()) {
            jdbcTemplate.update(
                "update category set name = ?, position_number = ? where id = ?",
                category.getName(),
                category.getPositionNumber(),
                category.getId());
        } else {
            throw new DuplicateCategoryException("A megadott név már foglalt.");
        }
    }

    public void deleteCategoryUpdateProducts(long id) {
        jdbcTemplate.update("update products set category_id = null where category_id = ?", id);
    }

    public void deleteCategory(long id) {
        jdbcTemplate.update("delete from category where id = ?", id);
    }

    public void updateCategoryPosition(long position) {
        jdbcTemplate.update("update category set position_number = position_number + 1 where position_number >= ?", position);
    }

    public void updateCategoryPositionMinus(Long posOld, Long posNew) {
        jdbcTemplate.update("update category set position_number = position_number - 1 " +
                        "where position_number > ? AND position_number <= ?",
                posOld, posNew );
    }

    public void updateCategoryPositionPlus(Long posOld, Long posNew) {
        jdbcTemplate.update("update category set position_number = position_number + 1 " +
                        "where position_number < ? AND position_number >= ?",
                posOld, posNew );
    }

    public void updateCategoryPositionAfterDelete(Long position) {
        jdbcTemplate.update("update category set position_number = position_number - 1 " +
                "where position_number >= ?", position );
    }
}
