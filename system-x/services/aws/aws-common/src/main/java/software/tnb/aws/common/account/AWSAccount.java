package software.tnb.aws.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AWSAccount implements Account, WithId {
    private String access_key;
    private String secret_key;
    private String region;
    private String account_id;

    @Override
    public String credentialsId() {
        return "aws";
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String accessKey() {
        return access_key;
    }

    public String secretKey() {
        return secret_key;
    }

    public String region() {
        return region;
    }

    public String accountId() {
        return account_id;
    }
}
