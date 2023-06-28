package software.tnb.jms.rabbitmq.account;

import software.tnb.common.account.Account;

public class RabbitmqAccount implements Account {
    private String username = "admin";
    private String password = "admin123.admin123.admin123.";

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
