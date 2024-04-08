package software.tnb.samba.account;

import software.tnb.common.account.Account;

public class SambaAccount implements Account {
    private String user = "camel";
    private String password = "camelTester123";

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }
}
