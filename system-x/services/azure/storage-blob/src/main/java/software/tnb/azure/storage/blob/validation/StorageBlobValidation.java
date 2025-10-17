package software.tnb.azure.storage.blob.validation;

import software.tnb.azure.common.account.AzureStorageAccount;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StorageBlobValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(StorageBlobValidation.class);

    private final BlobServiceClient client;
    private final AzureStorageAccount account;

    public StorageBlobValidation(BlobServiceClient client, AzureStorageAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createBlobContainer(String name) {
        LOG.debug("Creating blob {}", name);
        client.createBlobContainer(name);
    }

    public void deleteBlobContainer(String name) {
        LOG.debug("Deleting blob {}", name);
        try {
            client.deleteBlobContainer(name);
        } catch (BlobStorageException e) {
            // ignore an exception when trying to delete a non-existing container
            if (!e.getMessage().toLowerCase().contains("containernotfound")) {
                throw e;
            }
        }
    }

    public void createBlockBlob(String blobContainer, String blob, String message) {
        LOG.debug("Creating block blob with name {} and content {}", blob, message);
        ByteArrayInputStream dataStream = new ByteArrayInputStream(message.getBytes());
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).upload(dataStream, message.length());
    }

    public void createAppendBlob(String blobContainer, String blob, String message) {
        LOG.debug("Creating append blob with name {} and content {}", blob, message);
        ByteArrayInputStream dataStream = new ByteArrayInputStream(message.getBytes());
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).getAppendBlobClient().create(true);
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).getAppendBlobClient().appendBlock(dataStream, message.length());
    }

    public String readBlockBlob(String blobContainer, String blob) {
        LOG.debug("Reading block blob content with name {}", blob);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).downloadStream(outputStream);
        return outputStream.toString();
    }

    public List<BlobItem> getBlockBlobs(String blobContainer) {
        LOG.debug("Getting block blobs from blob container {}", blobContainer);
        return client.getBlobContainerClient(blobContainer).listBlobs().stream().collect(Collectors.toList());
    }

    public boolean blobExists(String blobContainer, String blob) {
        return blobContainerExists(blobContainer) && client.getBlobContainerClient(blobContainer).getBlobClient(blob).exists();
    }

    public boolean blobContainerExists(String containerName) {
        return client.getBlobContainerClient(containerName).exists();
    }

    public String createSasToken(String containerName) {
        BlobContainerClient blobClient = new BlobContainerClientBuilder()
            .endpoint(String.format("https://%s.blob.core.windows.net/%s", account.accountName(), account.accessKey()))
            .containerName(containerName)
            .credential(new StorageSharedKeyCredential(account.accountName(), account.accessKey()))
            .buildClient();

        // Create a SAS token that's valid for 1 day, as an example
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);

        // Assign permissions to the SAS token
        BlobContainerSasPermission blobContainerSasPermission = new BlobContainerSasPermission()
            .setWritePermission(true)
            .setListPermission(true)
            .setCreatePermission(true)
            .setDeletePermission(true)
            .setAddPermission(true)
            .setReadPermission(true);

        BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(expiryTime, blobContainerSasPermission);

        return blobClient.generateSas(sasSignatureValues);
    }
}
