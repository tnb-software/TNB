package org.jboss.fuse.tnb.sql.mariadb.service;

import org.jboss.fuse.tnb.sql.common.service.Sql;
import org.jboss.fuse.tnb.sql.mariadb.account.MariadbAccount;

import java.util.Map;

public abstract class MariaDb extends Sql {

    @Override
    public String sqlImage() {
        return "registry.redhat.io/rhel8/mariadb-103:latest";
    }

    @Override
    public int port() {
        return 3306;
    }

    @Override
    public MariadbAccount account() {
        return new MariadbAccount();
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
