package org.jboss.fuse.tnb.aws.validation;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.s3.S3Client;

public class S3Validation {

    private static final Logger LOG = LoggerFactory.getLogger(S3Validation.class);

    private final S3Client client;
    private final AWSAccount account;

    public S3Validation(S3Client client, AWSAccount account) {
        this.client = client;
        this.account = account;
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

        List<String> filesInBucket = client.listObjects(b -> b.bucket(bucketName)).contents().stream().map(c -> c.key())
            .collect(Collectors.toList());
        return filesInBucket;
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
}
