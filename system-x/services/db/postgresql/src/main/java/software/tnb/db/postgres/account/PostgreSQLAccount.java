package software.tnb.db.postgres.account;

import software.tnb.db.common.account.SQLAccount;

public class PostgreSQLAccount implements SQLAccount {
    @Override
    public String username() {
        return "testuser";
    }

    @Override
    public String password() {
        return "supersecret";
    }

    @Override
    public String database() {
        return "testdb";
    }
}
