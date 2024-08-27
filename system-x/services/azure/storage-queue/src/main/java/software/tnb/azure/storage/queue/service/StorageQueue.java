package software.tnb.azure.storage.queue.service;

import software.tnb.azure.common.account.AzureStorageAccount;
import software.tnb.azure.storage.queue.validation.StorageQueueValidation;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import com.google.auto.service.AutoService;

@AutoService(StorageQueue.class)
public class StorageQueue extends Service<AzureStorageAccount, QueueServiceClient, StorageQueueValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(StorageQueue.class);

    protected QueueServiceClient client() {
        LOG.debug("Creating new Storage Queue client");
        return new QueueServiceClientBuilder()
            .endpoint(String.format("https://%s.queue.core.windows.net/%s", account().accountName(), account().accessKey()))
            .credential(new StorageSharedKeyCredential(account().accountName(), account().accessKey()))
            .buildClient();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Storage Queue validation");
        validation = new StorageQueueValidation(client());
    }
}
