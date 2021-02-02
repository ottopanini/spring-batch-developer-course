package io.me.itemvalidator.domain;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Customer(resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getDate(4));
    }
}
