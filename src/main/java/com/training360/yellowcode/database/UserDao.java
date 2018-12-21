package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.User;
import com.training360.yellowcode.dbTables.UserRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String loginName = resultSet.getString("user_name");
            String fullName = resultSet.getString("full_name");
            String password = resultSet.getString("password");
            UserRole role = UserRole.valueOf(resultSet.getString("role"));
            return new User(id, loginName, fullName, password, role);
        }
    }

    public long createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "insert into users(user_name, full_name, password, enabled, role) values(?, ?, ?, 1, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getLoginName());
                ps.setString(2, user.getFullName());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRole());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<User> listUsers() {
        return jdbcTemplate.query("select id, user_name, full_name, password, enabled, role from users",
                new UserMapper());
    }

    public void updateUser(long id, String name, String password) {
        jdbcTemplate.update(
                "update users set full_name = coalesce(?, full_name), password = coalesce(?, password) where id = ?",
                name, password, id);
    }


    public void deleteUser(long id) {
        jdbcTemplate.update("delete from users where id = ?", id);
        jdbcTemplate.update("update orders set user_id = null where user_id = ?", id);
    }

    public Optional<User> findUserByUserName(String userName) {
        try {
            User user = jdbcTemplate.queryForObject("select id, user_name, full_name, password, enabled, role from users where user_name = ?",
                    new UserMapper(),
                    userName);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }

    }

}
