package software.tnb.opensearch.account;

import software.tnb.common.account.Account;

public class OpensearchAccount implements Account {
    private String user = "admin";
    private String password = "RHCamelTest!1234";

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
