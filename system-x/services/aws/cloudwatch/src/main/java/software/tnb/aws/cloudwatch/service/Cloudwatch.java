package software.tnb.aws.cloudwatch.service;

import software.tnb.aws.cloudwatch.validation.CloudwatchValidation;
import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

@AutoService(Cloudwatch.class)
public class Cloudwatch extends AWSService<AWSAccount, CloudWatchClient, CloudwatchValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new Cloudwatch validation");
        validation = new CloudwatchValidation(client(CloudWatchClient.class));
    }
}
