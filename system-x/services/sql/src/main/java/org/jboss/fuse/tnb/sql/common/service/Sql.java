package org.jboss.fuse.tnb.sql.common.service;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.sql.common.account.SqlAccount;
import org.jboss.fuse.tnb.sql.common.validation.SqlValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class Sql implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Sql.class);

    private SqlValidation validation;

    public abstract SqlAccount account();

    protected abstract String jdbcConnectionUrl();

    public abstract java.lang.String sqlImage();

    public abstract java.lang.String hostname();

    public abstract int port();

    public SqlValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Sql validation");
            validation = new SqlValidation(jdbcConnectionUrl(), account());
        }
        return validation;
    }

    public abstract Map<java.lang.String, java.lang.String> containerEnvironment();
}
