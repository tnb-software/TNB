package software.tnb.aws.common.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.configuration.AWSConfiguration;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.validation.Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkClient;

public abstract class AWSService<A extends AWSAccount, C extends SdkClient, V extends Validation>
    extends ConfigurableService<A, C, V, AWSConfiguration> {
    protected LocalStack localStack;

    protected static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

    @Override
    protected void defaultConfiguration() {
        getConfiguration().useLocalstack(false);
    }

    @Override
    public A account() {
        if (account == null) {
            Class<A> accountClass = (Class<A>) ReflectionUtil.getGenericTypesOf(AWSService.class, this.getClass())[0];
            account = AccountFactory.create(accountClass);
        }
        return account;
    }

    protected C client() {
        if (client == null) {
            Class<C> clientClass = (Class<C>) ReflectionUtil.getGenericTypesOf(AWSService.class, this.getClass())[1];
            client = AWSClient.createDefaultClient(account(), clientClass, getConfiguration().isLocalstack() ? localStack.clientUrl() : null);
        }
        return client;
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
