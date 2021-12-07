package org.jboss.fuse.tnb.lambda.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.common.service.ServiceFactory;
import org.jboss.fuse.tnb.iam.service.IAM;
import org.jboss.fuse.tnb.lambda.validation.LambdaValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.lambda.LambdaClient;

@AutoService(Lambda.class)
public class Lambda extends AWSService<AWSAccount, LambdaClient, LambdaValidation> {
    private final IAM iam = ServiceFactory.create(IAM.class);

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        super.afterAll(context);
        iam.afterAll(context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        iam.beforeAll(context);
        LOG.debug("Creating new Lambda validation");
        validation = new LambdaValidation(client(LambdaClient.class), iam);
    }
}
