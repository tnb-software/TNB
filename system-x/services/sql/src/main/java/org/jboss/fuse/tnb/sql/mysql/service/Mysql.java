package org.jboss.fuse.tnb.sql.mysql.service;

import org.jboss.fuse.tnb.sql.common.service.Sql;
import org.jboss.fuse.tnb.sql.mysql.account.MysqlAccount;

import java.util.Map;

public abstract class Mysql extends Sql {

    @Override
    public String sqlImage() {
        return "registry.redhat.io/rhel8/mysql-80:latest";
    }

    @Override
    public int port() {
        return 3306;
    }

    @Override
    public MysqlAccount account() {
        return new MysqlAccount();
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
