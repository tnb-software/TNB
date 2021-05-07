package org.jboss.fuse.tnb.azure.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class StorageBlobValidation {

    private static final Logger LOG = LoggerFactory.getLogger(StorageBlobValidation.class);

    private final BlobServiceClient client;

    public StorageBlobValidation(BlobServiceClient client) {
        this.client = client;
    }

    public void createBlobContainer(String name) {
        LOG.debug("Creating blob {}", name);
        client.createBlobContainer(name);
    }

    public void deleteBlobContainer(String name) {
        LOG.debug("Deleting blob {}", name);
        client.deleteBlobContainer(name);
    }

    public void creteBlockBlob(String blobContainer, String blob, String message) {
        LOG.debug("Creating block blob with name {} and content {}", blob, message);
        ByteArrayInputStream dataStream = new ByteArrayInputStream(message.getBytes());
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).upload(dataStream, message.length());
    }

    public String readBlockBlob(String blobContainer, String blob) {
        LOG.debug("Reading block blob content with name {}", blob);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        client.getBlobContainerClient(blobContainer).getBlobClient(blob).download(outputStream);
        return outputStream.toString();
    }

    public List<BlobItem> getBlockBlobs(String blobContainer) {
        LOG.debug("Getting block blobs from blob container {}", blobContainer);
        return client.getBlobContainerClient(blobContainer).listBlobs().stream().collect(Collectors.toList());
    }
}
