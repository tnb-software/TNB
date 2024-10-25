package software.tnb.elasticsearch.account;

import software.tnb.common.account.Account;

public class ElasticsearchAccount implements Account {
    private String user = "elastic";
    private String password = "password";

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
