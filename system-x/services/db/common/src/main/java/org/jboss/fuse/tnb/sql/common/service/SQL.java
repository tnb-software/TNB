package org.jboss.fuse.tnb.sql.common.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.deployment.WithExternalHostname;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.sql.common.account.SQLAccount;
import org.jboss.fuse.tnb.sql.common.validation.SQLValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class SQL implements Service, WithName, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(SQL.class);

    private SQLAccount account;
    private SQLValidation validation;

    protected abstract Class<? extends SQLAccount> accountClass();

    public SQLAccount account() {
        if (account == null) {
            account = Accounts.get(accountClass());
        }
        return account;
    }

    public abstract String jdbcConnectionUrl();

    public abstract String image();

    public abstract String hostname();

    public abstract int port();

    public SQLValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new SQL validation");
            validation = new SQLValidation(localConnectionUrl(), account());
        }
        return validation;
    }

    /**
     * Override this method in case the default replace doesn't work for the given type of database
     * @return local connection url (through port-forward in case of openshift)
     */
    protected String localConnectionUrl() {
        return jdbcConnectionUrl().replace("://" + hostname(), "://" + externalHostname());
    }

    public abstract Map<String, String> containerEnvironment();

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
