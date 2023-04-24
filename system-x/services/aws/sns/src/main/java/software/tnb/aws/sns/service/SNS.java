package software.tnb.aws.sns.service;

import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.sns.account.SNSAccount;
import software.tnb.aws.sns.validation.SNSValidation;
import software.tnb.aws.sqs.service.SQS;
import software.tnb.aws.sqs.validation.SQSValidation;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.ServiceFactory;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.sns.SnsClient;

@AutoService(SNS.class)
public class SNS extends AWSService<SNSAccount, SnsClient, SNSValidation> {
    private SQS sqs;

    @Override
    public SNSAccount account() {
        if (account == null) {
            LOG.debug("Creating new SNS account");
            account = AccountFactory.create(SNSAccount.class);

            if (getConfiguration().isLocalstack()) {
                account.setAccount_id("000000000000");
            }

            // There two are derived other values
            account.setTopicUrlPrefix(String.format("https://sns.%s.amazonaws.com/%s/", account.region(), account.accountId()));
            account.setTopicArnPrefix(String.format("arn:aws:sns:%s:%s:", account.region(), account.accountId()));
        }
        return account;
    }

    public SQSValidation getSQSValidation() {
        return sqs.validation();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        sqs.afterAll(extensionContext);
        super.afterAll(extensionContext);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        sqs = ServiceFactory.create(SQS.class, config -> config.useLocalstack(getConfiguration().isLocalstack()));
        sqs.beforeAll(extensionContext);
        LOG.debug("Creating new SNS validation");
        validation = new SNSValidation(client(), account(), sqs);
    }
}
