package org.jboss.fuse.tnb.kinesis.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.kinesis.validation.KinesisValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

@AutoService(Kinesis.class)
public class Kinesis implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(Kinesis.class);

    private AWSAccount account;
    private KinesisClient client;
    private KinesisValidation validation;

    public AWSAccount account() {
        if (account == null) {
            LOG.debug("Creating new Kinesis account");
            account = Accounts.get(AWSAccount.class);
        }
        return account;
    }

    protected KinesisClient client() {
        if (client == null) {
            LOG.debug("Creating new Kinesis client");
            client = KinesisClient.builder()
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
                .build();
        }
        return client;
    }

    public KinesisValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Kinesis validation");
            validation = new KinesisValidation(client(), account());
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
