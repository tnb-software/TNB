package org.jboss.fuse.tnb.azure.service;

import org.jboss.fuse.tnb.azure.account.StorageQueueAccount;
import org.jboss.fuse.tnb.azure.validation.StorageQueueValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import com.google.auto.service.AutoService;

@AutoService(StorageQueue.class)
public class StorageQueue implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(StorageQueue.class);

    private StorageQueueAccount account;
    private QueueServiceClient client;
    private StorageQueueValidation validation;

    public StorageQueueAccount account() {
        if (account == null) {
            account = Accounts.get(StorageQueueAccount.class);
        }
        return account;
    }

    public QueueServiceClient client() {
        if (client == null) {
            client = new QueueServiceClientBuilder()
                .endpoint(String.format("https://%s.queue.core.windows.net/%s", account().accountName(), account().accessKey()))
                .credential(new StorageSharedKeyCredential(account().accountName(), account().accessKey()))
                .buildClient();
        }
        return client;
    }

    public StorageQueueValidation validation() {
        if (validation == null) {
            validation = new StorageQueueValidation(client());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }
}
