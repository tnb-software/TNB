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

import com.unboundid.ldap.sdk.LDAPConnectionPool;

public class LDAP<A extends LDAPAccount, C extends LDAPConnectionPool, V extends LDAPValidation>
    extends ConfigurableService<A, C, V, LDAPConfiguration> {

    protected static final int PORT = 389;

    protected LDAPLocalStack localStack;

    public String url() {
        return getConfiguration().isRemoteUrl()
            ? String.format("ldap://%s:%s", account().getHost(), PORT)
            : localStack.url();
    }

    public LDAPConnectionPool getConnection() {
        return localStack.getConnection();
    }

    public C client() {
        return (C) getConnection();
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
