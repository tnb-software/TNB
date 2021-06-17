package org.jboss.fuse.tnb.iam.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.iam.validation.IAMValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

@AutoService(IAM.class)
public class IAM implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(IAM.class);

    private AWSAccount account;
    private IamClient client;
    private IAMValidation validation;

    public AWSAccount account() {
        if (account == null) {
            account = Accounts.get(AWSAccount.class);
        }
        return account;
    }

    protected IamClient client() {
        if (client == null) {
            LOG.debug("Creating new IAM client");
            client = IamClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(() -> AwsBasicCredentials.create(account().accessKey(), account().secretKey()))
                .build();
        }
        return client;
    }

    public IAMValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new IAM validation");
            validation = new IAMValidation(client());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // no-op
    }
}
