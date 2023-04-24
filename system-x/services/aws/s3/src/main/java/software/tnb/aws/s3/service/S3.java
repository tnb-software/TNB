package software.tnb.aws.s3.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.s3.validation.S3Validation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.s3.S3Client;

@AutoService(S3.class)
public class S3 extends AWSService<AWSAccount, S3Client, S3Validation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new S3 validation");
        validation = new S3Validation(client());
    }
}
