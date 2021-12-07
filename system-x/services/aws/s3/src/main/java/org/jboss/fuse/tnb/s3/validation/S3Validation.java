package org.jboss.fuse.tnb.s3.validation;

import org.jboss.fuse.tnb.common.service.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Validation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(S3Validation.class);

    private final S3Client client;

    public S3Validation(S3Client client) {
        this.client = client;
    }

    public void createS3Bucket(String name) {
        LOG.debug("Creating S3 bucket {}", name);
        client.createBucket(b -> b.bucket(name));
    }

    public void waitForBucket(String name) {
        LOG.debug("Waiting for S3 bucket to be ready {}", name);
        client.waiter().waitUntilBucketExists(builder -> builder.bucket(name));
    }

    public void deleteS3Bucket(String name) {
        deleteS3BucketContent(name);
        LOG.debug("Deleting S3 bucket {}", name);
        client.deleteBucket(b -> b.bucket(name));
    }

    public void deleteS3BucketContent(String name) {
        LOG.debug("Deleting all content of S3 bucket {}", name);
        client.listObjectsV2(b -> b.bucket(name)).contents().forEach(c -> client.deleteObject(b -> b.bucket(name).key(c.key())));
    }

    public List<String> listKeysInBucket(String bucketName) {
        return client.listObjects(b -> b.bucket(bucketName)).contents().stream().map(S3Object::key).collect(Collectors.toList());
    }

    public String readFileFromBucket(String bucketName, String key) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            client.getObject(b -> b.bucket(bucketName).key(key)).transferTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to transfer object contents", e);
        }
        return outputStream.toString();
    }

    public void createFile(String bucketName, String key, String content) {
        createFromBody(bucketName, key, RequestBody.fromString(content));
    }

    public void createFile(String bucketName, Path file) {
        createFile(bucketName, file.getFileName().toString(), file);
    }

    public void createFile(String bucketName, String key, Path file) {
        createFromBody(bucketName, key, RequestBody.fromFile(file));
    }

    private void createFromBody(String bucketName, String key, RequestBody body) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        client.putObject(objectRequest, body);
    }
}
