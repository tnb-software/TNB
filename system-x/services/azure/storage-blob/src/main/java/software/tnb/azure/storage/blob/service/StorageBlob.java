package software.tnb.azure.storage.blob.service;

import software.tnb.azure.common.account.AzureAccount;
import software.tnb.azure.storage.blob.validation.StorageBlobValidation;
import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.google.auto.service.AutoService;

@AutoService(StorageBlob.class)
public class StorageBlob implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(StorageBlob.class);

    private AzureAccount account;
    private StorageBlobValidation validation;

    public AzureAccount account() {
        if (account == null) {
            account = Accounts.get(AzureAccount.class);
        }
        return account;
    }

    protected BlobServiceClient client() {
        LOG.debug("Creating new Azure Storage Blob client");
        return new BlobServiceClientBuilder()
            .endpoint(String.format("https://%s.blob.core.windows.net/%s", account().accountName(), account().accessKey()))
            .credential(new StorageSharedKeyCredential(account().accountName(), account().accessKey()))
            .buildClient();
    }

    public StorageBlobValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Azure Storage Blob validation");
        validation = new StorageBlobValidation(client());
    }
}
