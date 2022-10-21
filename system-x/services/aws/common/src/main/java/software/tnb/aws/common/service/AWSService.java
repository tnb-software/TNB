package software.tnb.aws.common.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.client.AWSClient;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.common.service.Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkClient;

public abstract class AWSService<A extends AWSAccount, C extends SdkClient, V extends Validation> implements Service {
    protected static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

    protected A account;
    protected C client;
    protected V validation;

    public A account() {
        if (account == null) {
            account = (A) AccountFactory.create(AWSAccount.class);
        }
        return account;
    }

    protected C client(Class<C> clazz) {
        if (client == null) {
            client = AWSClient.createDefaultClient(account(), clazz);
        }
        return client;
    }

    public V validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
