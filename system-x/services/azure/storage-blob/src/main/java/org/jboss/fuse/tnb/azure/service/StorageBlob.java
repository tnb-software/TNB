package org.jboss.fuse.tnb.azure.service;

import org.jboss.fuse.tnb.azure.account.StorageBlobAccount;
import org.jboss.fuse.tnb.azure.validation.StorageBlobValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

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

    private StorageBlobAccount account;
    private BlobServiceClient client;
    private StorageBlobValidation validation;

    public StorageBlobAccount account() {
        if (account == null) {
            account = Accounts.get(StorageBlobAccount.class);
        }
        return account;
    }

    protected BlobServiceClient client() {
        if (client == null) {
            LOG.debug("Creating new Azure Storage Blob client");
            client = new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/%s", account().accountName(), account().accessKey()))
                .credential(new StorageSharedKeyCredential(account().accountName(), account().accessKey()))
                .buildClient();
        }
        return client;
    }

    public StorageBlobValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Azure Storage Blob validation");
            validation = new StorageBlobValidation(client());
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
