package org.jboss.fuse.tnb.aws.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.client.AWSClient;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.common.service.Validation;

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
            account = (A) Accounts.get(AWSAccount.class);
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
