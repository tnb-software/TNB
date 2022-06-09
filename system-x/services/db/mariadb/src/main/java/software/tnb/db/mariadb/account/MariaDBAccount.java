package software.tnb.db.mariadb.account;

import software.tnb.db.common.account.SQLAccount;

public class MariaDBAccount implements SQLAccount {
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
