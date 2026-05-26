package software.tnb.db.oracle.account;

import software.tnb.db.common.account.SQLAccount;

public class OracleAccount implements SQLAccount {
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
        return "FREEPDB1";
    }
}
