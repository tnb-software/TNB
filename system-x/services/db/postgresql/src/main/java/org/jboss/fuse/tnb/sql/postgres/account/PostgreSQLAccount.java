package org.jboss.fuse.tnb.sql.postgres.account;

import org.jboss.fuse.tnb.sql.common.account.SQLAccount;

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
