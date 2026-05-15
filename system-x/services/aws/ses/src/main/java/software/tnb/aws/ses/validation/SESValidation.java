package software.tnb.aws.ses.validation;

import software.tnb.aws.ses.account.AWSSESAccount;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.ses.SesClient;

public class SESValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(SESValidation.class);

    private final SesClient client;
    private final AWSSESAccount account;

    public SESValidation(SesClient client, AWSSESAccount account) {
        this.client = client;
        this.account = account;
    }
}
