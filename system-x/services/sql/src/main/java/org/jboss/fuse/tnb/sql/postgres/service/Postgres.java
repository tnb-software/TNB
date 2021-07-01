package org.jboss.fuse.tnb.sql.postgres.service;

import org.jboss.fuse.tnb.sql.common.service.Sql;
import org.jboss.fuse.tnb.sql.postgres.account.PostgresAccount;

import java.util.Map;

public abstract class Postgres extends Sql {

    @Override
    public java.lang.String sqlImage() {
        return "registry.redhat.io/rhel8/postgresql-13:latest";
    }

    @Override
    public int port() {
        return 5432;
    }

    @Override
    public PostgresAccount account() {
        return new PostgresAccount();
    }

    @Override
    public Map<java.lang.String, java.lang.String> containerEnvironment() {
        return Map.of(
            "POSTGRESQL_DATABASE", account().database(),
            "POSTGRESQL_USER", account().username(),
            "POSTGRESQL_PASSWORD", account().password()
        );
    }
}
