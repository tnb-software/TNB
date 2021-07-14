package org.jboss.fuse.tnb.amq.account;

import org.jboss.fuse.tnb.common.account.Account;

public class AmqBrokerAccount implements Account {
    private String username = "admin";
    private String password = "admin123.";

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
