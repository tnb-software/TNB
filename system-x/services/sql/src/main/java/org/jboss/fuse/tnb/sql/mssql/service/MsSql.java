package org.jboss.fuse.tnb.sql.mssql.service;

import org.jboss.fuse.tnb.sql.common.service.Sql;
import org.jboss.fuse.tnb.sql.mssql.account.MsSqlAccount;

import java.util.Map;

public abstract class MsSql extends Sql {

    @Override
    public String sqlImage() {
        return "mcr.microsoft.com/mssql/rhel/server:2019-latest";
    }

    @Override
    public int port() {
        return 1433;
    }

    @Override
    public MsSqlAccount account() {
        return new MsSqlAccount();
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "ACCEPT_EULA", "Y",
            "SA_PASSWORD", account().password()
        );
    }
}
