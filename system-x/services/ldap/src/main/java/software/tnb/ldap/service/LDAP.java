package software.tnb.ldap.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.service.Service;
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

    protected LDAPLocalStack localStack;

    protected LDAPRemoteStack remoteStack;

    public String url() {
        return getConfiguration().isRemoteUrl()
            ? remoteStack.url()
            : localStack.url();
    }

    public LDAPConnectionPool getConnection() {
        return getConfiguration().isRemoteUrl()
            ? remoteStack.getConnection()
            : localStack.getConnection();
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

    public Service getCurrentStack() {
        Service ldapService = getConfiguration().isRemoteUrl()
            ? remoteStack
            : localStack;
        return ldapService;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }

        if (!getConfiguration().isRemoteUrl()) {
            localStack.afterAll(extensionContext);
        } else {
            remoteStack.afterAll(extensionContext);
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!getConfiguration().isRemoteUrl()) {
            localStack = ServiceFactory.create(LDAPLocalStack.class);
            localStack.beforeAll(extensionContext);
        } else {
            remoteStack = ServiceFactory.create(LDAPRemoteStack.class);
            remoteStack.beforeAll(extensionContext);
        }
    }
}
