package org.jboss.fuse.tnb.firehose.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.common.service.ServiceFactory;
import org.jboss.fuse.tnb.firehose.validation.KinesisFirehoseValidation;
import org.jboss.fuse.tnb.iam.service.IAM;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.firehose.FirehoseClient;

@AutoService(KinesisFirehose.class)
public class KinesisFirehose extends AWSService<AWSAccount, FirehoseClient, KinesisFirehoseValidation> {
    private final IAM iam = ServiceFactory.create(IAM.class);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        iam.beforeAll(extensionContext);
        LOG.debug("Creating new Kinesis validation");
        validation = new KinesisFirehoseValidation(client(FirehoseClient.class), iam);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        iam.afterAll(extensionContext);
        super.afterAll(extensionContext);
    }
}
