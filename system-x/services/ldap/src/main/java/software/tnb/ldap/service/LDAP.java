package software.tnb.ldap.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.service.ServiceFactory;
import software.tnb.ldap.account.LDAPAccount;
import software.tnb.ldap.account.LocalLDAPAccount;
import software.tnb.ldap.account.RemoteLDAPAccount;
import software.tnb.ldap.service.configuration.LDAPConfiguration;
import software.tnb.ldap.validation.LDAPValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.lang3.StringUtils;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

public class LDAP<A extends LDAPAccount, C extends LDAPConnectionPool, V extends LDAPValidation>
    extends ConfigurableService<A, C, V, LDAPConfiguration> {

    protected static final int PORT = 389;

    protected LDAPLocalStack localStack;

    public String url() {
        return getConfiguration().isRemoteUrl()
            ? String.format("ldap://%s:%s", account().getHost(), PORT)
            : localStack.url();
    }

    public C client() {
        final LDAPConnection ldapConnection = new LDAPConnection();
        try {
            String url = url();
            int port = PORT;
            if (!getConfiguration().isRemoteUrl()) {
                url = "localhost";
                port = Integer.parseInt(StringUtils.substringAfter(localStack.url(), "host:"));
            }
            ldapConnection.connect(url, port, 20000);
            ldapConnection.bind(account().getUsername(), account().getPassword());
            client = (C) new LDAPConnectionPool(ldapConnection, 1);
        } catch (LDAPException e) {
            throw new RuntimeException("Error when connecting to LDAP server: " + e.getMessage());
        }

        return client;
    }

    public V validation() {
        if (validation == null) {
            validation = (V) new LDAPValidation(client());
        }
        return validation;
    }

    @Override
    public A account() {
        if (account == null) {
            account = getConfiguration().isRemoteUrl()
                ? (A) AccountFactory.create(RemoteLDAPAccount.class)
                : (A) AccountFactory.create(LocalLDAPAccount.class);
        }
        return account;
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().useRemoteUrl(false);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }

        if (localStack != null) {
            localStack.afterAll(extensionContext);
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!getConfiguration().isRemoteUrl()) {
            localStack = ServiceFactory.create(LDAPLocalStack.class);
            localStack.beforeAll(extensionContext);
        }
    }
}
