package org.jboss.fuse.tnb.cloudwatch.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.cloudwatch.validation.CloudwatchValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

@AutoService(Cloudwatch.class)
public class Cloudwatch implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(Cloudwatch.class);

    private AWSAccount account;
    private CloudWatchClient client;
    private CloudwatchValidation validation;

    public AWSAccount account() {
        if (account == null) {
            LOG.debug("Creating new Cloudwatch account");
            account = Accounts.get(AWSAccount.class);
        }
        return account;
    }

    protected CloudWatchClient client() {
        LOG.debug("Creating new Cloudwatch client");
        client = CloudWatchClient.builder()
            .region(Region.of(account().region()))
            .credentialsProvider(() -> AwsBasicCredentials.create(account.accessKey(), account().secretKey()))
            .build();
        return client;
    }

    public CloudwatchValidation validation() {
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
        LOG.debug("Creating new Cloudwatch validation");
        validation = new CloudwatchValidation(client(), account());
    }
}
