package software.tnb.google.storage.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class GoogleStorageValidation {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleStorageValidation.class);

    private final Storage client;

    public GoogleStorageValidation(Storage client) {
        this.client = client;
    }

    public void createBucket(String bucketName) {
        createBucket(bucketName, "us");
    }

    public void createBucket(String bucketName, String location) {
        LOG.info("Creating bucket \"{}\" in region \"{}\"", bucketName, location);
        client.create(
            BucketInfo.newBuilder(bucketName)
                .setLocation(location)
                .build());
    }

    public void createFile(String bucketName, String fileName, String content) {
        LOG.info("Creating a new file \"{}\" with content \"{}\" in \"{}\" bucket", fileName, content, bucketName);
        client.create(BlobInfo.newBuilder(bucketName, fileName).build(), content.getBytes());
    }

    public void deleteBucket(String bucketName) {
        LOG.info("Deleting bucket \"{}\"", bucketName);
        listFiles(bucketName).forEach(f -> deleteFile(bucketName, f));
        client.delete(bucketName);
    }

    public List<String> listFiles(String bucketName) {
        List<String> fileNames = new ArrayList<>();
        client.list(bucketName).iterateAll().forEach(b -> fileNames.add(b.getBlobId().getName()));
        return fileNames;
    }

    public String getFileContent(String bucketName, String fileName) {
        return new String(client.readAllBytes(bucketName, fileName));
    }

    public void deleteFile(String bucketName, String fileName) {
        LOG.info("Deleting file \"{}\" in bucket \"{}\"", fileName, bucketName);
        client.delete(BlobId.of(bucketName, fileName));
    }
}
