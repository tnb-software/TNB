package org.jboss.fuse.tnb.s3.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.s3.validation.S3Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@AutoService(S3.class)
public class S3 implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3.class);

    private AWSAccount account;
    private S3Client client;
    private S3Validation validation;

    public AWSAccount account() {
        if (account == null) {
            account = Accounts.get(AWSAccount.class);
        }
        return account;
    }

    protected S3Client client() {
        if (client == null) {
            LOG.debug("Creating new S3 client");
            client = S3Client.builder()
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
                .build();
        }
        return client;
    }

    public S3Validation validation() {
        if (validation == null) {
            LOG.debug("Creating new S3 validation");
            validation = new S3Validation(client(), account());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }
}
