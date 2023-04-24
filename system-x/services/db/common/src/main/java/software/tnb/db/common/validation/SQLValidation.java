package software.tnb.db.common.validation;

import software.tnb.common.validation.Validation;
import software.tnb.db.common.account.SQLAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class SQLValidation implements Validation {
    private final String jdbcConnectionUrl;
    private final SQLAccount account;

    public SQLValidation(String jdbcConnectionUrl, SQLAccount account) {
        this.jdbcConnectionUrl = jdbcConnectionUrl;
        this.account = account;
    }

    public boolean execute(String sql) {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            return conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute query", e);
        }
    }

    public ResultSet executeQuery(String sql) {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute query", e);
        }
    }

    public void checkQueryResult(String sql, Consumer<ResultSet> check) {
        try (Connection conn = DriverManager.getConnection(jdbcConnectionUrl, account.username(), account.password())) {
            check.accept(conn.createStatement().executeQuery(sql));
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute query", e);
        }
    }
}
