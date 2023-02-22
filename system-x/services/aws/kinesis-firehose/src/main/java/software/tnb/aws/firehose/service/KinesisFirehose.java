package software.tnb.aws.firehose.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.firehose.validation.KinesisFirehoseValidation;
import software.tnb.aws.iam.service.IAM;
import software.tnb.common.service.ServiceFactory;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.firehose.FirehoseClient;

@AutoService(KinesisFirehose.class)
public class KinesisFirehose extends AWSService<AWSAccount, FirehoseClient, KinesisFirehoseValidation> {
    private final IAM iam = ServiceFactory.create(IAM.class);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
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
