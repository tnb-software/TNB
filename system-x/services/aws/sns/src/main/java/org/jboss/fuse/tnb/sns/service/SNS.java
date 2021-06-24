package org.jboss.fuse.tnb.sns.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.common.service.ServiceFactory;
import org.jboss.fuse.tnb.sns.account.SNSAccount;
import org.jboss.fuse.tnb.sns.validation.SNSValidation;
import org.jboss.fuse.tnb.sqs.service.SQS;
import org.jboss.fuse.tnb.sqs.validation.SQSValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@AutoService(SNS.class)
public class SNS implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(SNS.class);

    private SNSAccount account;
    private SnsClient client;
    private SNSValidation validation;
    private final SQS sqs = ServiceFactory.create(SQS.class);

    public SNSAccount account() {
        if (account == null) {
            LOG.debug("Creating new SNS account");
            account = Accounts.get(SNSAccount.class);
            // There two are derived other values
            account.setTopicUrlPrefix(String.format("https://sns.%s.amazonaws.com/%s/", account.region(), account.accountId()));
            account.setTopicArnPrefix(String.format("arn:aws:sns:%s:%s:", account.region(), account.accountId()));
        }
        return account;
    }

    protected SnsClient client() {
        LOG.debug("Creating new SNS client");
        client = SnsClient.builder()
            .region(Region.of(account().region()))
            .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
            .build();
        return client;
    }

    public SNSValidation validation() {
        return validation;
    }

    public SQSValidation getSQSValidation() {
        return sqs.validation();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (client != null) {
            client.close();
        }
        sqs.afterAll(extensionContext);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        sqs.beforeAll(extensionContext);
        LOG.debug("Creating new SNS validation");
        validation = new SNSValidation(client(), account(), sqs);
    }
}
