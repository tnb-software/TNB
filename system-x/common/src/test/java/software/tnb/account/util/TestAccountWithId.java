package software.tnb.account.util;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class TestAccountWithId implements Account, WithId {
    private String userName;
    private String password;
    private int account_id;

    @Override
    public String credentialsId() {
        return "test-acc";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int accountId() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}
