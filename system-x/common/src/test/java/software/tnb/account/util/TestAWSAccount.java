package software.tnb.account.util;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class TestAWSAccount implements Account, WithId {
    private String access_key;
    private String secret_key;

    @Override
    public String credentialsId() {
        return "aws";
    }

    public String access_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String secret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }
}
