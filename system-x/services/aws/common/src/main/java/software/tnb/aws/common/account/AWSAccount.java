package software.tnb.aws.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AWSAccount implements Account, WithId {
    private String accessKey;
    private String secretKey;
    private String region = "us-west-1";
    private String accountId;

    @Override
    public String credentialsId() {
        return "aws";
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String accessKey() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }

    public String region() {
        return region;
    }

    public String accountId() {
        return accountId;
    }
}
