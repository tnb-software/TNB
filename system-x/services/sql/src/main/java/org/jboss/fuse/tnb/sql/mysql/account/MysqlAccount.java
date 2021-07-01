package org.jboss.fuse.tnb.sql.mysql.account;

import org.jboss.fuse.tnb.sql.common.account.SqlAccount;

public class MysqlAccount implements SqlAccount {
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
