package software.tnb.infinispan.account;

import software.tnb.common.account.Account;

public class InfinispanAccount implements Account {

    private String username = "admin";
    private String password = "passwordpasswordpasswordpassword";

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
