package org.jboss.fuse.tnb.sql.mysql.service;

import org.jboss.fuse.tnb.sql.common.account.SQLAccount;
import org.jboss.fuse.tnb.sql.common.service.SQL;
import org.jboss.fuse.tnb.sql.mysql.account.MySQLAccount;

import java.util.Map;

public abstract class MySQL extends SQL {
    protected static final int PORT = 3306;

    @Override
    public String image() {
        return "registry.redhat.io/rhel8/mysql-80:latest";
    }

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return MySQLAccount.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", hostname(), port(), account().database());
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "MYSQL_DATABASE", account().database(),
            "MYSQL_USER", account().username(),
            "MYSQL_PASSWORD", account().password()
        );
    }
}
