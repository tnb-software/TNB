package org.jboss.fuse.tnb.sqs.service;

import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.sqs.account.SQSAccount;
import org.jboss.fuse.tnb.sqs.validation.SQSValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.sqs.SqsClient;

@AutoService(SQS.class)
public class SQS extends AWSService<SQSAccount, SqsClient, SQSValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(SQS.class);

    @Override
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

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new SQS validation");
        validation = new SQSValidation(client(SqsClient.class), account());
    }
}
