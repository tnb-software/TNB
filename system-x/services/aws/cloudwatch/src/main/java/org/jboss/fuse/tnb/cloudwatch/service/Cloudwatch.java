package org.jboss.fuse.tnb.cloudwatch.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.cloudwatch.validation.CloudwatchValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

@AutoService(Cloudwatch.class)
public class Cloudwatch extends AWSService<AWSAccount, CloudWatchClient, CloudwatchValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Cloudwatch validation");
        validation = new CloudwatchValidation(client(CloudWatchClient.class));
    }
}
