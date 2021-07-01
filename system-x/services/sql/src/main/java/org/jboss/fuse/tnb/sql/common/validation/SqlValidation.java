package org.jboss.fuse.tnb.sql.common.validation;

import org.jboss.fuse.tnb.sql.common.account.SqlAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SqlValidation {
    private final String jdbcConnectionUrl;
    private final SqlAccount account;

    public SqlValidation(String jdbcConnectionUrl, SqlAccount account) {
        this.jdbcConnectionUrl = jdbcConnectionUrl;
        this.account = account;
    }

    public boolean execSql(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            return conn.createStatement().execute(sql);
        }
    }

    public ResultSet execQuery(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            return conn.createStatement().executeQuery(sql);
        }
    }

    public void checkQueryResult(String sql, Consumer<ResultSet> check) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            check.accept(conn.createStatement().executeQuery(sql));
        }
    }
}
