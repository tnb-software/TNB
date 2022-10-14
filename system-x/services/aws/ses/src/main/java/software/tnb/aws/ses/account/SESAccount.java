package software.tnb.aws.ses.account;

import software.tnb.aws.common.account.AWSAccount;

import java.util.Map;

/**
 * You need to verify that you own either a domain or an email address, so the identities need to be pre-configured in SES
 */
public class SESAccount extends AWSAccount {
    private Map<String, String> identities;

    public String identity(String key) {
        return identities.get(key);
    }

    public void setIdentities(Map<String, String> identities) {
        this.identities = identities;
    }
}
