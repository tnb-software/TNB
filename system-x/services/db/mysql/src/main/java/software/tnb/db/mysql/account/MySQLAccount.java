package software.tnb.db.mysql.account;

import software.tnb.db.common.account.SQLAccount;

public class MySQLAccount implements SQLAccount {
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
