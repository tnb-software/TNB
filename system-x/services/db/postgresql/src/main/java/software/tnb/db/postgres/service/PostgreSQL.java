package software.tnb.db.postgres.service;

import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.SQL;
import software.tnb.db.postgres.account.PostgreSQLAccount;

import java.util.Map;

public abstract class PostgreSQL extends SQL {
    protected static final int PORT = 5432;

    @Override
    public String defaultImage() {
        return "registry.redhat.io/rhel8/postgresql-13:latest";
    }

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return PostgreSQLAccount.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s", host(), port(), account().database());
    }

    @Override
    public Map<java.lang.String, java.lang.String> containerEnvironment() {
        return Map.of(
            "POSTGRESQL_DATABASE", account().database(),
            "POSTGRESQL_USER", account().username(),
            "POSTGRESQL_PASSWORD", account().password()
        );
    }
}
