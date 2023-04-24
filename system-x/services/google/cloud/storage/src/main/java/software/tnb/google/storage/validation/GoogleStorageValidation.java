package software.tnb.google.storage.validation;

import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class GoogleStorageValidation implements Validation {
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

    public void uploadFile(String bucketName, Path file) {
        try {
            LOG.info("Uploading file \"{}\" to \"{}\" bucket", file.toAbsolutePath(), bucketName);
            client.create(BlobInfo.newBuilder(bucketName, file.getFileName().toString()).build(), Files.readAllBytes(file));
            WaitUtils.waitFor(() -> listFiles(bucketName).contains(file.getFileName().toString()), "Waiting until the file "
                + file.getFileName() + " is present in bucket " + bucketName);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file bytes", e);
        }
    }

    public void deleteBucket(String bucketName) {
        LOG.info("Deleting bucket \"{}\"", bucketName);
        try {
            listFiles(bucketName).forEach(f -> deleteFile(bucketName, f));
            client.delete(bucketName);
        } catch (StorageException e) {
            if (!e.getMessage().contains("bucket does not exist")) {
                throw e;
            }
        }
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

    public boolean bucketExists(String bucketName) {
        return StreamSupport.stream(client.list().iterateAll().spliterator(), false).anyMatch(b -> bucketName.equals(b.asBucketInfo().getName()));
    }
}
