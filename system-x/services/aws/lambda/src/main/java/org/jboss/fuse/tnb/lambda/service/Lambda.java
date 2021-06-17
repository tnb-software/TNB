package org.jboss.fuse.tnb.lambda.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.common.service.ServiceFactory;
import org.jboss.fuse.tnb.iam.service.IAM;
import org.jboss.fuse.tnb.lambda.validation.LambdaValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

@AutoService(Lambda.class)
public class Lambda implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Lambda.class);

    private AWSAccount account;
    private LambdaClient client;
    private LambdaValidation validation;
    private final IAM iam = ServiceFactory.create(IAM.class);

    public AWSAccount account() {
        if (account == null) {
            account = Accounts.get(AWSAccount.class);
        }
        return account;
    }

    protected LambdaClient client() {
        if (client == null) {
            LOG.debug("Creating new Lambda client");
            client = LambdaClient.builder()
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account().accessKey(), account().secretKey()))
                .build();
        }
        return client;
    }

    public LambdaValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Lambda validation");
            validation = new LambdaValidation(client(), iam);
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (client != null) {
            client.close();
        }
        iam.afterAll(context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // no-op
    }
}
