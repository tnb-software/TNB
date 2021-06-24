package org.jboss.fuse.tnb.firehose.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.firehose.account.KinesisFirehoseAccount;
import org.jboss.fuse.tnb.firehose.validation.KinesisFirehoseValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;

@AutoService(KinesisFirehose.class)
public class KinesisFirehose implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(KinesisFirehose.class);

    private KinesisFirehoseAccount account;
    private FirehoseClient client;
    private KinesisFirehoseValidation validation;

    public KinesisFirehoseAccount account() {
        if (account == null) {
            LOG.debug("Creating new Kinesis account");
            account = Accounts.get(KinesisFirehoseAccount.class);
        }
        return account;
    }

    protected FirehoseClient client() {
        LOG.debug("Creating new Kinesis client");
        client = FirehoseClient.builder()
            .region(Region.of(account().region()))
            .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
            .build();
        return client;
    }

    public KinesisFirehoseValidation validation() {
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
        LOG.debug("Creating new Kinesis validation");
        validation = new KinesisFirehoseValidation(client(), account());
    }
}
