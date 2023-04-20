package software.tnb.aws.common.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.configuration.AWSConfiguration;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.service.Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkClient;

public abstract class AWSService<A extends AWSAccount, C extends SdkClient, V extends Validation> extends ConfigurableService<AWSConfiguration> {
    protected LocalStack localStack;

    protected static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

    protected A account;
    protected C client;
    protected V validation;

    @Override
    protected void defaultConfiguration() {
        getConfiguration().useLocalstack(false);
    }

    public A account() {
        if (account == null) {
            account = (A) AccountFactory.create(AWSAccount.class);
        }
        return account;
    }

    protected C client(Class<C> clazz) {
        if (client == null) {
            client = AWSClient.createDefaultClient(account(), clazz, getConfiguration().isLocalstack() ? localStack.clientUrl() : null);
        }
        return client;
    }

    public V validation() {
        return validation;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (getConfiguration().isLocalstack()) {
            localStack = ServiceFactory.create(LocalStack.class);
            localStack.beforeAll(extensionContext);
        }
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

    public String serviceUrl() {
        if (localStack == null) {
            throw new IllegalStateException("AWSService#serviceUrl was called, but wasn't configured to use LocalStack");
        }
        return localStack.serviceUrl();
    }
}
