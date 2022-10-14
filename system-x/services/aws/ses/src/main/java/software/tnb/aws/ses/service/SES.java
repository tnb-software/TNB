package software.tnb.aws.ses.service;

import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.ses.account.SESAccount;
import software.tnb.aws.ses.validation.SESValidation;
import software.tnb.common.account.Accounts;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.ses.SesClient;

@AutoService(SES.class)
public class SES extends AWSService<SESAccount, SesClient, SESValidation> {
    @Override
    public SESAccount account() {
        if (account == null) {
            LOG.debug("Creating new SES account");
            account = Accounts.get(SESAccount.class);
        }
        return account;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new SQS validation");
        validation = new SESValidation(client(SesClient.class), account());
    }
}
