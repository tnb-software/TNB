package software.tnb.db.mariadb.service;

import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.SQL;
import software.tnb.db.mariadb.account.MariaDBAccount;

import java.util.Map;

public abstract class MariaDB extends SQL {
    protected static final int PORT = 3306;

    @Override
    public String image() {
        return "registry.redhat.io/rhel8/mariadb-103:latest";
    }

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return MariaDBAccount.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:mariadb://%s:%d/%s", hostname(), port(), account().database());
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "MYSQL_DATABASE", account().database(),
            "MYSQL_USER", account().username(),
            "MYSQL_PASSWORD", account().password()
        );
    }
}
