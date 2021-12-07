package org.jboss.fuse.tnb.kinesis.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.kinesis.validation.KinesisValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.kinesis.KinesisClient;

@AutoService(Kinesis.class)
public class Kinesis extends AWSService<AWSAccount, KinesisClient, KinesisValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Kinesis validation");
        validation = new KinesisValidation(client(KinesisClient.class));
    }
}
