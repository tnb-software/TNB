package software.tnb.aws.kinesis.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.kinesis.validation.KinesisValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.kinesis.KinesisClient;

@AutoService(Kinesis.class)
public class Kinesis extends AWSService<AWSAccount, KinesisClient, KinesisValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new Kinesis validation");
        validation = new KinesisValidation(client(KinesisClient.class));
    }
}
