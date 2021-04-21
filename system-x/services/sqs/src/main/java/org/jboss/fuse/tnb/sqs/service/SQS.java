package org.jboss.fuse.tnb.sqs.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.sqs.account.SQSAccount;
import org.jboss.fuse.tnb.sqs.validation.SQSValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@AutoService(SQS.class)
public class SQS implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(SQS.class);

    private SQSAccount account;
    private SqsClient client;
    private SQSValidation validation;

    public SQSAccount account() {
        if (account == null) {
            LOG.debug("Creating new SQS account");
            account = Accounts.get(SQSAccount.class);
            // There two are derived other values
            account.setQueueUrlPrefix(String.format("https://sqs.%s.amazonaws.com/%s/", account.region(), account.accountId()));
            account.setQueueArnPrefix(String.format("arn:aws:sqs:%s:%s:", account.region(), account.accountId()));
        }
        return account;
    }

    public SqsClient client() {
        if (client == null) {
            LOG.debug("Creating new SQS client");
            client = SqsClient.builder()
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
                .build();
        }
        return client;
    }

    public SQSValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new SQS validation");
            validation = new SQSValidation(client(), account());
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
