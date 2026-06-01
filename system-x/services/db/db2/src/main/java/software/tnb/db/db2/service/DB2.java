package software.tnb.db.db2.service;

import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.SQL;
import software.tnb.db.db2.account.DB2Account;

import java.util.Map;

public abstract class DB2 extends SQL {
    protected static final int PORT = 50000;

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return DB2Account.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:db2://%s:%d/%s", host(), port(), account().database());
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "LICENSE", "accept",
            "DB2INST1_PASSWORD", account().password(),
            "DBNAME", account().database()
        );
    }

    @Override
    public String defaultImage() {
        return "icr.io/db2_community/db2:12.1.4.0";
    }

    @Override
    public int port() {
        return PORT;
    }
}
