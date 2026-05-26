package software.tnb.db.oracle.service;

import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.SQL;
import software.tnb.db.oracle.account.OracleAccount;

import java.util.Map;

public abstract class Oracle extends SQL {
    protected static final int PORT = 1521;

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return OracleAccount.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:oracle:thin:@//%s:%d/%s", host(), port(), account().database());
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "APP_USER", account().username(),
            "APP_USER_PASSWORD", account().password(),
            "ORACLE_PASSWORD", account().password()
        );
    }

    @Override
    public String defaultImage() {
        return "mirror.gcr.io/gvenzl/oracle-free:23-slim-faststart";
    }

    @Override
    public int port() {
        return PORT;
    }
}
