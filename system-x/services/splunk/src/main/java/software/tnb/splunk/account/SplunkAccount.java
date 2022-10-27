package software.tnb.splunk.account;

import software.tnb.common.account.Account;

public class SplunkAccount implements Account {

    private String username = "admin";

    private String password;

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
