package software.tnb.aws.lambda.service;

import software.tnb.aws.lambda.validation.LambdaValidation;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.common.service.ServiceFactory;
import software.tnb.aws.iam.service.IAM;

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
