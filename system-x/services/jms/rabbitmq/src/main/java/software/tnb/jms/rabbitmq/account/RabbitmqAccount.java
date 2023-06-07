package software.tnb.jms.rabbitmq.account;

import software.tnb.common.account.Account;

public class RabbitmqAccount implements Account {
    private String username = "guest";
    private String password = "guest";

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
