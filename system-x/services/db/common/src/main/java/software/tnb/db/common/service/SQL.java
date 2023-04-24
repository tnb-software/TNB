package software.tnb.db.common.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.service.Service;
import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.validation.SQLValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class SQL extends Service<SQLAccount, NoClient, SQLValidation> implements WithName, WithExternalHostname, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(SQL.class);

    protected abstract Class<? extends SQLAccount> accountClass();

    public SQLAccount account() {
        if (account == null) {
            account = AccountFactory.create(accountClass());
        }
        return account;
    }

    public abstract String jdbcConnectionUrl();

    public abstract String hostname();

    public abstract int port();

    public int localPort() {
        return port();
    }

    @Override
    public SQLValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new SQL validation");
            validation = new SQLValidation(localConnectionUrl(), account());
        }
        return validation;
    }

    /**
     * Override this method in case the default replace doesn't work for the given type of database
     *
     * @return local connection url (through port-forward in case of openshift)
     */
    protected String localConnectionUrl() {
        return jdbcConnectionUrl()
            .replace("://" + hostname(), "://" + externalHostname())
            .replace(":" + port(), ":" + localPort());
    }

    public abstract Map<String, String> containerEnvironment();

    @Override
    public String externalHostname() {
        return "localhost";
    }
}
