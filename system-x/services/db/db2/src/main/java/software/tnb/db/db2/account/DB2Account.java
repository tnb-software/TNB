package software.tnb.db.db2.account;

import software.tnb.db.common.account.SQLAccount;

public class DB2Account implements SQLAccount {
    @Override
    public String username() {
        return "db2inst1";
    }

    @Override
    public String password() {
        return "dbpwd123";
    }

    @Override
    public String database() {
        return "TNB";
    }
}

