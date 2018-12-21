package com.training360.yellowcode.database;

import com.training360.yellowcode.dbTables.Dashboard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardDao {

    private JdbcTemplate jdbcTemplate;

    public DashboardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long userCounter() {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM users WHERE role = 'ROLE_USER';", Long.class);
    }

    public Long productCounter() {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM products;", Long.class);
    }

    public Long activeProductCounter() {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM products WHERE status = 'ACTIVE';", Long.class);
    }

    public Long orderCounter() {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM orders;", Long.class);
    }

    public Long activeOrderCounter() {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM orders WHERE status = 'ACTIVE';", Long.class);
    }

    public Dashboard createDashboard() {
        return new Dashboard(userCounter(), activeProductCounter(), productCounter(), activeOrderCounter(), orderCounter());
    }
}
