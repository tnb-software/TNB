package software.tnb.azure.storage.datalake.service;

import software.tnb.azure.common.account.AzureStorageAccount;
import software.tnb.azure.storage.datalake.validation.StorageDataLakeValidation;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import com.google.auto.service.AutoService;

@AutoService(StorageDataLake.class)
public class StorageDataLake extends Service<AzureStorageAccount, DataLakeServiceClient, StorageDataLakeValidation> {

    private static final Logger LOG = LoggerFactory.getLogger(StorageDataLake.class);

    protected DataLakeServiceClient client() {
        LOG.debug("Creating new Azure Storage DataLake client");
        return new DataLakeServiceClientBuilder()
            .endpoint(String.format("https://%s.dfs.core.windows.net/%s", account().accountName(), account().accessKey()))
            .credential(new StorageSharedKeyCredential(account().accountName(), account().accessKey()))
            .buildClient();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Azure Storage DataLake validation");
        validation = new StorageDataLakeValidation(client(), account());
    }
}
