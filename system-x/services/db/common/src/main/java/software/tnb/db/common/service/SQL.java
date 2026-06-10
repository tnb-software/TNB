package software.tnb.db.common.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.client.NoClient;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.configuration.SQLConfiguration;
import software.tnb.db.common.validation.SQLValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SQL extends ConfigurableService<SQLAccount, NoClient, SQLValidation, SQLConfiguration>
    implements WithName, WithExternalHostname, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(SQL.class);
    private static final Pattern VERSION_EXTRACT = Pattern.compile("(\\d+(?:\\.\\d+)*)");

    private String cachedDbVersion;
    private String cachedDriverVersion;
    private boolean metadataResolved;

    protected abstract Class<? extends SQLAccount> accountClass();

    public SQLAccount account() {
        if (account == null) {
            account = AccountFactory.create(accountClass());
        }
        return account;
    }

    public abstract String jdbcConnectionUrl();

    public abstract String host();

    public abstract int port();

    public int localPort() {
        return port();
    }

    @Override
    public String serviceVersion() {
        resolveMetadata();
        if (cachedDbVersion != null) {
            return cachedDbVersion;
        }
        return super.serviceVersion();
    }

    @Override
    public String driverVersion() {
        resolveMetadata();
        return cachedDriverVersion;
    }

    private void resolveMetadata() {
        if (metadataResolved) {
            return;
        }
        if (validation == null) {
            return;
        }
        metadataResolved = true;
        try {
            SQLValidation.DatabaseMetadata meta = validation.getDatabaseMetadata();
            if (meta != null && meta.dbVersion() != null) {
                Matcher m = VERSION_EXTRACT.matcher(meta.dbVersion());
                cachedDbVersion = m.find() ? m.group(1) : meta.dbVersion();
                cachedDriverVersion = meta.driverVersion();
            }
        } catch (Exception e) {
            LOG.debug("Could not resolve database metadata", e);
        }
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
        return OpenshiftConfiguration.isOpenshift()
            ? jdbcConnectionUrl()
            .replace("://" + host(), "://" + externalHostname())
            .replace(":" + port(), ":" + localPort())
            : jdbcConnectionUrl();
    }

    public abstract Map<String, String> containerEnvironment();

    @Override
    public String externalHostname() {
        return "localhost";
    }

    public void restart(Runnable onContainerStopped) {
        throw new IllegalArgumentException("Not implemented");
    }

    public String name() {
        return ReflectionUtil.getSuperClassName(this.getClass());
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().environmentVariables(containerEnvironment());
    }
}
