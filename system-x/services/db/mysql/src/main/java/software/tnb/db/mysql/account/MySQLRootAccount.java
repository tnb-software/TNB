package software.tnb.db.mysql.account;

import software.tnb.db.common.account.SQLAccount;

public class MySQLRootAccount implements SQLAccount {

    @Override
    public String username() {
        return "root";
    }

    @Override
    public String password() {
        return "my-secret-pw";
    }

    @Override
    public String database() {
        return "testdb";
    }

}
