package software.tnb.aws.ses.service;

import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.ses.account.SESAccount;
import software.tnb.aws.ses.validation.SESValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.ses.SesClient;

@AutoService(SES.class)
public class SES extends AWSService<SESAccount, SesClient, SESValidation> {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new SES validation");
        validation = new SESValidation(client(), account());
    }
}
