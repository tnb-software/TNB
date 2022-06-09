package software.tnb.db.mssql.account;

import software.tnb.db.common.account.SQLAccount;

public class MSSQLAccount implements SQLAccount {
    @Override
    public String username() {
        return "sa";
    }

    @Override
    public String password() {
        return "supersecret0!";
    }

    @Override
    public String database() {
        return "master";
    }
}
