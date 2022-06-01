package org.jboss.fuse.tnb.sql.mariadb.account;

import org.jboss.fuse.tnb.sql.common.account.SQLAccount;

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
