package org.jboss.fuse.tnb.sql.mssql.account;

import org.jboss.fuse.tnb.sql.common.account.SqlAccount;

public class MsSqlAccount implements SqlAccount {
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
