package org.jboss.fuse.tnb.cassandra.service;

import org.jboss.fuse.tnb.cassandra.account.CassandraAccount;
import org.jboss.fuse.tnb.cassandra.validation.CassandraValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import com.datastax.oss.driver.api.core.CqlSession;

public abstract class Cassandra implements Service {

    public static final int CASSANDRA_PORT = 9042;

    private CassandraValidation validation;
    private CassandraAccount account;

    public String image() {
        // official library image required hacks in openshift, bitnami works out of the box
        return "docker.io/bitnami/cassandra:4.0";
    }

    public CassandraValidation validation() {
        if (validation == null) {
            validation = new CassandraValidation(session());
        }
        return validation;
    }

    protected abstract CqlSession session();

    public abstract int port();

    public abstract String host();

    public String cassandraUrl(String keyspace) {
        return String.format("cql:%s:%s/%s?username=%s&password=%s", host(), port(), keyspace, account().username(), account().password());
    }

    public CassandraAccount account() {
        if (account == null) {
            account = Accounts.get(CassandraAccount.class);
        }
        return account;
    }
}
