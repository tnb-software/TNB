package org.jboss.fuse.tnb.s3.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.s3.validation.S3Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.s3.S3Client;

@AutoService(S3.class)
public class S3 extends AWSService<AWSAccount, S3Client, S3Validation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new S3 validation");
        validation = new S3Validation(client(S3Client.class));
    }
}
