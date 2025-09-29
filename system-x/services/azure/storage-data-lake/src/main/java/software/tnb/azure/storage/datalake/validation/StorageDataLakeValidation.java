package software.tnb.azure.storage.datalake.validation;

import software.tnb.azure.common.account.AzureStorageAccount;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.DataLakeFileSystemClientBuilder;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.models.DataLakeStorageException;
import com.azure.storage.file.datalake.models.PathItem;
import com.azure.storage.file.datalake.sas.DataLakeServiceSasSignatureValues;
import com.azure.storage.file.datalake.sas.FileSystemSasPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StorageDataLakeValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(StorageDataLakeValidation.class);

    private final DataLakeServiceClient client;
    private final AzureStorageAccount account;

    public StorageDataLakeValidation(DataLakeServiceClient client, AzureStorageAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createDataLakeFileSystem(String name) {
        LOG.debug("Creating DFS {}", name);
        client.createFileSystem(name);
    }

    public void deleteDataLakeFileSystem(String name) {
        LOG.debug("Deleting DFS {}", name);
        try {
            client.deleteFileSystem(name);
        } catch (DataLakeStorageException e) {
            // ignore an exception when trying to delete a non-existing file
            if (!e.getMessage().toLowerCase().contains("containernotfound")) {
                throw e;
            }
        }
    }

    public void uploadDataLakeFile(String fileSystem, String fileName, String message) {
        LOG.debug("Creating block DataLake with name {} and content {}", fileName, message);
        ByteArrayInputStream dataStream = new ByteArrayInputStream(message.getBytes());
        client.getFileSystemClient(fileSystem).getFileClient(fileName).upload(dataStream, message.length());
    }

    public String readDataLakeFile(String fileSystem, String fileName) {
        LOG.debug("Reading block DataLake content with name {}", fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        client.getFileSystemClient(fileSystem).getFileClient(fileName).read(outputStream);
        return outputStream.toString();
    }

    public List<PathItem> getBlockDataLakes(String fileSystem) {
        LOG.debug("Getting block DataLake file from DataLake fileSystem {}", fileSystem);
        return client.getFileSystemClient(fileSystem).listPaths().stream().collect(Collectors.toList());
    }

    public boolean dataLakeFileExists(String fileSystem, String fileName) {
        return dataLakeFileSystemExists(fileSystem) && client.getFileSystemClient(fileSystem).getFileClient(fileName).exists();
    }

    public boolean dataLakeFileSystemExists(String fileSystem) {
        return client.getFileSystemClient(fileSystem).exists();
    }

    public String createSasToken(String fileName) {
        DataLakeFileSystemClient dataLakeClient = new DataLakeFileSystemClientBuilder()
            .endpoint(String.format("https://%s.dfs.core.windows.net/%s", account.accountName(), account.accessKey()))
            .fileSystemName(fileName)
            .credential(new StorageSharedKeyCredential(account.accountName(), account.accessKey()))
            .buildClient();

        // Create a SAS token that's valid for 1 day, as an example
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);

        // Assign permissions to the SAS token
        FileSystemSasPermission dataLakeFileSystemSasPermission = new FileSystemSasPermission()
            .setWritePermission(true)
            .setListPermission(true)
            .setCreatePermission(true)
            .setDeletePermission(true)
            .setAddPermission(true)
            .setReadPermission(true);

        DataLakeServiceSasSignatureValues sasSignatureValues = new DataLakeServiceSasSignatureValues(expiryTime, dataLakeFileSystemSasPermission);

        return dataLakeClient.generateSas(sasSignatureValues);
    }
}
